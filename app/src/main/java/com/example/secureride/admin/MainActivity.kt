package com.example.secureride.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.secureride.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // Declare the binding variable
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the binding variable
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up button click listeners
        binding.btnLogin.setOnClickListener {
            navigateTo(UserTypeLogin::class.java)
        }
        binding.btnSignup.setOnClickListener {
            navigateTo(UserTypeRegistration::class.java)
        }
    }

    private fun <T> navigateTo(destination: Class<T>) {
        startActivity(Intent(this, destination))
    }
}
