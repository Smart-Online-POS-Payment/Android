package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Make sure this matches your layout file name

        setupSignUpTextView()

        val signInButton: Button = findViewById(R.id.signinButton)
        signInButton.setOnClickListener {
            val emailEditText = findViewById<EditText>(R.id.editText1)
            val passwordEditText = findViewById<EditText>(R.id.editText2)

            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signInUser(email, password)
            } else {
                Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_LONG).show()
            }
        }
        val forgotPasswordTextView: TextView = findViewById(R.id.textViewForgotPassword)
        forgotPasswordTextView.setOnClickListener {
            sendPasswordResetEmail()
        }
    }

    private fun signInUser(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null && user.isEmailVerified) {
                        // Email is verified, proceed to the home page
                        val intent = Intent(this, HomePageActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Email is not verified
                        Toast.makeText(this, "Please verify your email first.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // Authentication failed, show an error message
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
    private fun setupSignUpTextView() {
        val textViewSignUp: TextView = findViewById(R.id.textViewSignUp)
        val spannableString = SpannableString(textViewSignUp.text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@MainActivity, SignUpActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false // Set to true if you want underline
                ds.color = ds.linkColor // Set your custom color if needed
            }
        }

        val signUpTextStart = spannableString.indexOf("Create here")
        spannableString.setSpan(clickableSpan, signUpTextStart, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textViewSignUp.text = spannableString
        textViewSignUp.movementMethod = LinkMovementMethod.getInstance()
    }
    private fun sendPasswordResetEmail() {
        val emailEditText = findViewById<EditText>(R.id.editText1)
        val email = emailEditText.text.toString().trim()

        if (email.isNotEmpty()) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Password reset email sent.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Failed to send reset email: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            Toast.makeText(this, "Please enter your email address.", Toast.LENGTH_LONG).show()
        }
    }
}
