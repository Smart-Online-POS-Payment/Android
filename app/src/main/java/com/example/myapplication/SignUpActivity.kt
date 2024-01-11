package com.example.myapplication

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var backButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        firebaseAuth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signUpButton = findViewById(R.id.signUpButton)
        backButton = findViewById(R.id.buttonBack)
        progressBar = findViewById(R.id.progressBar)

        progressBar.visibility = View.GONE

        setUpSignUpButtonListener()
        backButton.setOnClickListener { finish() }
    }

    private fun setUpSignUpButtonListener() {
        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (validateInput(email, password)) {
                createAccount(email, password)
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() || password.isEmpty() -> {
                Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_LONG).show()
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_LONG).show()
                false
            }
            !isPasswordValid(password) -> {
                Toast.makeText(this, "Password must be at least 8 characters long and include a number, a lowercase letter, an uppercase letter, and a special character.", Toast.LENGTH_LONG).show()
                false
            }
            else -> true
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
        return password.matches(passwordPattern.toRegex())
    }

    private fun createAccount(email: String, password: String) {
        progressBar.visibility = View.VISIBLE

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progressBar.visibility = View.GONE

                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    sendEmailVerification(user)
                } else {
                    val message = task.exception?.localizedMessage ?: "Unknown error occurred"
                    Toast.makeText(this, "Account creation failed: $message", Toast.LENGTH_LONG).show()
                }
            }
    }


    private fun sendEmailVerification(user: FirebaseUser?) {
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Verification email sent to ${user.email}. Please verify your email.", Toast.LENGTH_LONG).show()
            } else {
                val message = task.exception?.localizedMessage ?: "Failed to send verification email"
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
