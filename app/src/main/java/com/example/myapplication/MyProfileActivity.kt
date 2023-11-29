package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MyProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        val ssnEditText: EditText = findViewById(R.id.TCKNEditText)
        val nameEditText: EditText = findViewById(R.id.nameEditText)
        val surnameEditText: EditText = findViewById(R.id.surnameEditText)
        val dobEditText: EditText = findViewById(R.id.dobEditText)
        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val changePasswordButton: Button = findViewById(R.id.changePasswordButton)
        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish() // This will close MyProfileActivity and return to HomePageActivity
        }
        // Example: Set the email field with the current user's email
        val currentUser = FirebaseAuth.getInstance().currentUser
        emailEditText.setText(currentUser?.email)

        // Set a dummy password text
        passwordEditText.setText("********")

        // Set OnClickListener for the Change Password button
        changePasswordButton.setOnClickListener {
            // To be implemented
        }

        // Load and display other user data like SSN, Name, etc.
        // This data must be retrieved from your database or user management system
    }

}
