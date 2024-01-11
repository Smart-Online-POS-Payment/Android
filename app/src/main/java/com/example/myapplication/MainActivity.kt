package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Ensure this matches your layout file name

        setupClickableTextViews()

        val emailEditText = findViewById<EditText>(R.id.editText1)
        val passwordEditText = findViewById<EditText>(R.id.editText2)
        val signInButton = findViewById<Button>(R.id.signinButton)

        signInButton.setOnClickListener {
            attemptSignIn(emailEditText, passwordEditText)
        }

        setupEditorActions(emailEditText, passwordEditText)
    }

    override fun onResume() {
        super.onResume()
        clearInputFields()
    }

    private fun setupClickableTextViews() {
        setupSignUpTextView()
        setupForgotPasswordTextView()
    }

    private fun setupEditorActions(emailEditText: EditText, passwordEditText: EditText) {
        val editorActionListener = TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptSignIn(emailEditText, passwordEditText)
                return@OnEditorActionListener true
            }
            false
        }
        emailEditText.setOnEditorActionListener(editorActionListener)
        passwordEditText.setOnEditorActionListener(editorActionListener)
    }

    private fun clearInputFields() {
        findViewById<EditText>(R.id.editText1).text.clear()
        findViewById<EditText>(R.id.editText2).text.clear()
    }

    private fun attemptSignIn(emailEditText: EditText, passwordEditText: EditText) {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            signInUser(email, password)
        } else {
            Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_LONG).show()
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
        val signUpTextView = findViewById<TextView>(R.id.textViewSignUp)
        val spannableString = SpannableString(signUpTextView.text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@MainActivity, SignUpActivity::class.java))
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
                ds.color = ds.linkColor // Customize this as needed
            }
        }

        val signUpTextStart = spannableString.indexOf("Create here")
        spannableString.setSpan(clickableSpan, signUpTextStart, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        signUpTextView.text = spannableString
        signUpTextView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setupForgotPasswordTextView() {
        val forgotPasswordTextView = findViewById<TextView>(R.id.textViewForgotPassword)
        forgotPasswordTextView.setOnClickListener {
            sendPasswordResetEmail()
        }
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
