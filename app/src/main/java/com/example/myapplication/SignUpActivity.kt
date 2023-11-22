package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up) // Make sure this matches your layout file name

        val signButton: Button = findViewById(R.id.signinButton2)
        signButton.setOnClickListener {

            val editTextEmail = findViewById<EditText>(R.id.editText11)
            val editTextPassword = findViewById<EditText>(R.id.editText21)

            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            // Instance of FirebaseAuth
            val firebaseAuth = FirebaseAuth.getInstance()

            // Creating a new user
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign-up success
                    Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                    // Optionally, redirect to another activity after sign-up
                } else {
                    // Sign-up failed
                    Toast.makeText(this, "Failed to create account", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
