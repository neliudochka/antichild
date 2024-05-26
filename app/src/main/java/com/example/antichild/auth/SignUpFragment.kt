package com.example.antichild.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.antichild.MotionDetectionFragment
import com.example.antichild.R
import com.example.antichild.databinding.FragmentSignUpBinding
import com.example.antichild.models.Child
import com.example.antichild.models.Parent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        binding.username.addTextChangedListener {
            checkFields()
        }

        binding.email.addTextChangedListener {
            checkFields()
        }

        binding.password.addTextChangedListener {
            checkFields()
        }

        binding.repeatPassword.addTextChangedListener {
            checkFields()
        }

        binding.roleGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.role_parent -> {
                    binding.accessPassword.visibility = View.VISIBLE
                    binding.parentEmail.visibility = View.GONE
                    binding.accessPasswordText.visibility = View.VISIBLE
                    binding.parentEmailText.visibility = View.GONE
                }

                R.id.role_child -> {
                    binding.accessPassword.visibility = View.GONE
                    binding.parentEmail.visibility = View.VISIBLE
                    binding.accessPasswordText.visibility = View.GONE
                    binding.parentEmailText.visibility = View.VISIBLE
                }
            }
        }

        binding.accessPassword.addTextChangedListener {
            checkFields()
        }

        binding.parentEmail.addTextChangedListener {
            checkFields()
        }

        binding.signUpButton.setOnClickListener {
            if (binding.roleChild.isChecked) {
                checkIfParentExists(binding.parentEmail.text.toString()) { exists ->
                    if (!exists) {
                        Toast.makeText(context, "Parent with this email doesn't exist", Toast.LENGTH_SHORT).show()
                    } else {
                        registerNewUser()
                    }
                }
            } else registerNewUser()
        }

        return binding.root
    }

    private fun checkFields() {
        val username = binding.username.text.toString()
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val repeatPassword = binding.repeatPassword.text.toString()
        val accessPassword = binding.accessPassword.text.toString()
        val parentEmail = binding.parentEmail.text.toString()

        val isRoleParent = binding.roleParent.isChecked
        val isRoleChild = binding.roleChild.isChecked

        binding.signUpButton.isEnabled = username.isNotEmpty() &&
                email.isNotEmpty() &&
                password.isNotEmpty() &&
                repeatPassword.isNotEmpty() &&
                ((isRoleParent && accessPassword.isNotEmpty()) || (isRoleChild && parentEmail.isNotEmpty()))
    }

    private fun registerNewUser() {
        val username = binding.username.text.toString()
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val repeatPassword = binding.repeatPassword.text.toString()
        val role = if (binding.roleParent.isChecked) "parent" else "child"
        val advance = if (role == "parent")
            binding.accessPassword.text.toString()
        else binding.parentEmail.text.toString()

        if (repeatPassword != password) {
            Toast.makeText(requireContext(), "Passwords do not match!", Toast.LENGTH_LONG).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("SignUpFragment", "createUserWithEmail:success: ${task.result.user?.uid}")

                    addUserToDb(username, email, role, advance)

                    parentFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, MotionDetectionFragment.newInstance())
                        .commit()
                } else {
                    Log.w("SignUpFragment", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(requireContext(), "Oops, something went wrong!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToDb(username: String, email: String, role: String, advance: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = if (role == "parent") {
            FirebaseDatabase.getInstance().getReference("/users/parent/$uid")
        } else {
            FirebaseDatabase.getInstance().getReference("/users/child/$uid")
        }

        val user = if (role == "parent") {
            Parent(uid, username, email, role, advance)
        } else {
            Child(uid, username, email, role, advance)
        }

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("SignUpFragment", "User added to database: $uid")
            }
            .addOnFailureListener { e ->
                Log.e("SignUpFragment", "Error adding user to database", e)
            }
    }

    private fun checkIfParentExists(email: String, callback: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val parentRef = database.getReference("/users/parent")

        parentRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val exists = snapshot.exists()
                    callback(exists)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("SignUpFragment", "Database error: ${error.message}")
                    callback(false)
                }
            })
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignUpFragment()
    }
}