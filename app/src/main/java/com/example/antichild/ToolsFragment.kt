package com.example.antichild

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.antichild.auth.LaunchFragment
import com.example.antichild.auth.SignInFragment
import com.example.antichild.databinding.FragmentToolsBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ToolsFragment : Fragment() {
    lateinit var binding: FragmentToolsBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentToolsBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        setButtonListeners()

        return binding.root
    }

    private fun setButtonListeners() {
        binding.motionAlarmButton.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, MotionDetectionFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        binding.logout.setOnClickListener {
            Firebase.auth.signOut()
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, LaunchFragment.newInstance())
                .commit()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ToolsFragment()
    }
}