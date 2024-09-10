package com.example.secureride.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.secureride.databinding.ActivityUserTypeLoginBinding
import com.example.secureride.databinding.ActivityUserTypeRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserTypeRegistration : AppCompatActivity() {

    private lateinit var binding: ActivityUserTypeRegistrationBinding
    private val firebaseDatabaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("user")
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserTypeRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve userType from Intent
        userType = intent.getStringExtra("user") ?: "UNKNOWN"

        binding.userDisplayName.text = "Create an $userType Account"

        binding.btnSignup.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString()
        val confirmPassword = binding.confirmPassword.text.toString()
        val firstName = binding.firstName.text.toString().trim()
        val lastName = binding.lastName.text.toString().trim()
        val phone = binding.phone.text.toString().trim()

        // Validate inputs
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            handleValidationErrors(firstName, lastName, email, phone, password, confirmPassword)
            return
        }

        if (password != confirmPassword) {
            binding.confirmPassword.error = "Password does not match!"
            Toast.makeText(this, "Password does not match!", Toast.LENGTH_SHORT).show()
            return
        }

        // Register user with Firebase Auth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Check if user is already registered
                    firebaseDatabaseReference.child(firebaseAuth.currentUser?.uid ?: "").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                Log.d("REGISTER", "$userType is already registered!")
                                Toast.makeText(this@UserTypeRegistration, "$userType is already registered!", Toast.LENGTH_SHORT).show()
                            } else {
                                saveUserDataToDatabase(email, firstName, lastName, phone, password)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("REGISTER", "Error in checking registration status: ${error.message}")
                            Toast.makeText(this@UserTypeRegistration, "Error checking registration status: ${error.message}", Toast.LENGTH_LONG).show()
                        }
                    })
                } else {
                    Log.e("REGISTER", "Registration error: ${task.exception?.message}")
                    binding.password.error = task.exception?.message
                    Toast.makeText(this@UserTypeRegistration, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun handleValidationErrors(firstName: String, lastName: String, email: String, phone: String, password: String, confirmPassword: String) {
        if (firstName.isEmpty()) binding.firstName.error = "Please enter your first name!"
        if (lastName.isEmpty()) binding.lastName.error = "Please enter your last name!"
        if (email.isEmpty()) binding.email.error = "Please enter your email!"
        if (phone.isEmpty()) binding.phone.error = "Please enter your phone number!"
        if (password.isEmpty()) binding.password.error = "Please enter your password!"
        if (confirmPassword.isEmpty()) binding.confirmPassword.error = "Please confirm your password!"
    }

    private fun saveUserDataToDatabase(email: String, firstName: String, lastName: String, phone: String, password: String) {
        val userId = firebaseAuth.currentUser?.uid ?: ""
        if (userId.isNotEmpty()) {
            val user = UserData(
                userId, email, firstName, lastName, phone, password, userType, false.toString()
            )

            firebaseDatabaseReference.child(userId).setValue(user)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("REGISTER", "$userType has been successfully registered!")
                        Toast.makeText(this, "$userType has been successfully registered!", Toast.LENGTH_SHORT).show()
                        firebaseAuth.signOut()
                        startActivity(Intent(this, UserTypeLogin::class.java))
                        finish()
                    } else {
                        Log.e("REGISTER", "Error saving user data: ${task.exception?.message}")
                        Toast.makeText(this, "Error saving user data: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Log.e("REGISTER", "Current user ID is null or empty.")
            Toast.makeText(this, "Current user ID is null or empty.", Toast.LENGTH_SHORT).show()
        }
    }
}
