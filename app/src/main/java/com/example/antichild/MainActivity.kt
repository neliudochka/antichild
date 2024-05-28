package com.example.antichild

import android.os.Bundle
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
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser

        SharedPreferencesHelper.init(this)

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
}