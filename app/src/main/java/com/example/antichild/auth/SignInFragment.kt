package com.example.antichild.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.antichild.MotionDetectionFragment
import com.example.antichild.R
import com.example.antichild.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth

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

                    parentFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, MotionDetectionFragment.newInstance())
                        .commit()
                } else {
                    Log.w("SignInFragment", "signInWithEmail:failure", task.exception)
                    Toast.makeText(requireContext(), "Authentication failed!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignInFragment()
    }
}