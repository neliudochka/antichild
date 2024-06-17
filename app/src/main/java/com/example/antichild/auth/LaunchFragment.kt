package com.example.antichild.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.antichild.R
import com.example.antichild.databinding.FragmentLaunchBinding

class LaunchFragment : Fragment() {
    private lateinit var binding: FragmentLaunchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLaunchBinding.inflate(inflater, container, false)

        binding.signIn.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, SignInFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        binding.signUp.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, SignUpFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = LaunchFragment()
    }
}