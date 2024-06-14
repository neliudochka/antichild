package com.example.antichild

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.antichild.auth.LaunchFragment
import com.example.antichild.databinding.ActivityMainBinding
import com.example.antichild.utils.SharedPreferencesHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val currentUser = auth.currentUser

        SharedPreferencesHelper.init(this)

        if (savedInstanceState == null) {
            if (currentUser != null) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, ToolsFragment.newInstance())
                    .commit()
            } else {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, LaunchFragment.newInstance())
                    .commit()
            }
        }

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            handleIntent(it)
        }
    }

    private fun handleIntent(intent: Intent) {
        val fragmentName = intent.getStringExtra("fragment")
        if (fragmentName != null) {
            openFragment(fragmentName)
        }
    }

    private fun openFragment(fragment: String) {
        Log.d("Please", ("ParentMotionDetectionFragment" == fragment).toString())
        when (fragment) {
            "ParentMotionDetectionFragment" -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, ParentMotionDetectionFragment())
                    .commit()
            }
            "MotionDetectionFragment" -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, MotionDetectionFragment())
                    .commit()
            }
            else -> return
        }
    }
}