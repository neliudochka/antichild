package com.example.antichild.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.example.antichild.R
import com.example.antichild.ToolsFragment
import com.example.antichild.databinding.FragmentSignInBinding
import com.example.antichild.models.Child
import com.example.antichild.models.Parent
import com.example.antichild.utils.SharedPreferencesHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        binding.usernameLogin.addTextChangedListener {
            checkFields()
        }

        binding.passwordLogin.addTextChangedListener {
            checkFields()
        }

        binding.signInButton.setOnClickListener {
            loginUser()
        }

        return binding.root
    }

    private fun checkFields() {
        val email = binding.usernameLogin.text.toString()
        val password = binding.passwordLogin.text.toString()

        binding.signInButton.isEnabled = email.isNotEmpty() && password.isNotEmpty()
    }

    private fun loginUser() {
        val email = binding.usernameLogin.text.toString()
        val password = binding.passwordLogin.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("SignInFragment", "signInWithEmail:success: ${task.result.user?.uid}")

                    val uid = FirebaseAuth.getInstance().uid ?: ""
                    checkChildUser(uid, email)

            } else {
                    Log.w("SignInFragment", "signInWithEmail:failure", task.exception)
                    Toast.makeText(requireContext(), "Authentication failed!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun checkChildUser(uid: String, email: String) {
        val childRef = FirebaseDatabase.getInstance().getReference("/users/child/$uid")

        childRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val childUser = snapshot.getValue(Child::class.java)
                    if (childUser != null) {
                        val username = childUser.username
                        val role = childUser.role

                        getParentsAccessPasswordByEmail(childUser.parentEmail, object : ParentPasswordCallback {
                            override fun onPasswordRetrieved(parentAccessPassword: String) {
                                SharedPreferencesHelper.saveUserData(
                                    uid,
                                    username,
                                    email,
                                    role,
                                    parentAccessPassword
                                )
                                navigateToToolsFragment()
                            }

                            override fun onError(error: String) {
                                Log.e("SignInFragment", error)
                            }
                        })
                    } else {
                        Log.e("SignInFragment", "Child user data is null")
                    }
                } else {
                    checkParentUser(uid, email)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("SignInFragment", "Failed to read value.", error.toException())
            }
        })
    }

    fun checkParentUser (uid: String, email: String) {
        val parentRef = FirebaseDatabase.getInstance().getReference("/users/parent/$uid")

        parentRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val parentUser = snapshot.getValue<Parent>()
                    if (parentUser != null) {
                        val username = parentUser.username
                        val role = parentUser.role
                        val parentAccessPassword = parentUser.accessPassword

                        SharedPreferencesHelper.saveUserData(
                            uid,
                            username,
                            email,
                            role,
                            parentAccessPassword
                        )

                        navigateToToolsFragment()
                    } else {
                        Log.e("SignInFragment", "Parent user data is null")
                    }
                } else {
                    Log.e("SignInFragment", "Snapshot does not exist")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("SignInFragment", "Failed to read value.", error.toException())
            }
        })
    }

    fun navigateToToolsFragment() {
        parentFragmentManager
            .popBackStack(null, POP_BACK_STACK_INCLUSIVE)

        parentFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, ToolsFragment.newInstance())
            .commit()
    }

    interface ParentPasswordCallback {
        fun onPasswordRetrieved(parentAccessPassword: String)
        fun onError(error: String)
    }

    fun getParentsAccessPasswordByEmail(parentEmail: String, callback: ParentPasswordCallback) {
        val parentRef = FirebaseDatabase.getInstance().getReference("/users/parent")

        parentRef.orderByChild("email").equalTo(parentEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (childSnapshot in snapshot.children) {
                            val parentUser = childSnapshot.getValue(Parent::class.java)
                            if (parentUser != null) {
                                callback.onPasswordRetrieved(parentUser.accessPassword)
                                return
                            } else {
                                callback.onError("Parent user data is null")
                            }
                        }
                    } else {
                        callback.onError("Snapshot does not exist")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback.onError(error.message)
                }
            })
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignInFragment()
    }
}