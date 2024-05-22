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
import com.example.antichild.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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

        binding.signUpButton.setOnClickListener {
            registerNewUser()
        }

        return binding.root
    }

    private fun checkFields() {
        val username = binding.username.text.toString()
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val repeatPassword = binding.repeatPassword.text.toString()

        binding.signUpButton.isEnabled = username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && repeatPassword.isNotEmpty()
    }

    private fun registerNewUser() {
        val username = binding.username.text.toString()
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val repeatPassword = binding.repeatPassword.text.toString()

        if (repeatPassword != password) {
            Toast.makeText(requireContext(), "Password and repeat password are not the same!", Toast.LENGTH_LONG).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("SignUpFragment", "createUserWithEmail:success: ${task.result.user?.uid}")

                    addUserToDb(username, email)

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

    private fun addUserToDb(username: String, email: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username, email)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("SignUpFragment", "User added to database: $uid")
            }
            .addOnFailureListener { e ->
                Log.e("SignUpFragment", "Error adding user to database", e)
            }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignUpFragment()
    }
}