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
import verifyAccount

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Make sure this matches your layout file name

        setupSignUpTextView()

        val signButton: Button = findViewById(R.id.signinButton)
        signButton.text = getString(R.string.sign_in)
        signButton.setOnClickListener {

            val editText1 = findViewById<EditText>(R.id.editText1)
            val editText2 = findViewById<EditText>(R.id.editText2)

            val email = editText1.text.toString()
            val password = editText2.text.toString()

            verifyAccount(email, password)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, HomePageActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "No account found", Toast.LENGTH_SHORT).show()
                }
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
}
