package com.example.antichild

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.antichild.auth.LaunchFragment
import com.example.antichild.databinding.FragmentToolsBinding
import com.example.antichild.models.Parent
import com.example.antichild.utils.SharedPreferencesHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ToolsFragment : Fragment() {
    private lateinit var binding: FragmentToolsBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToolsBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        addGreetingText()
        setButtonListeners()

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun addGreetingText(){
        val userdata = SharedPreferencesHelper.getUserData()
        binding.helloText.text = "hello " + userdata.username + "! role: " + userdata.role
    }
    private fun setButtonListeners() {
        val userdata = SharedPreferencesHelper.getUserData()
        if (userdata.role == "child") {
            binding.motionAlarmButton.setOnClickListener {
                parentFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, MotionDetectionFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }
        } else {
            binding.motionAlarmButton.setOnClickListener {
                parentFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, ParentMotionDetectionFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }
        }

        binding.logout.setOnClickListener {
            Firebase.auth.signOut()
            SharedPreferencesHelper.clearUserData()
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