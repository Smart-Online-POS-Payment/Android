package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import verifyAccount

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Make sure this matches your layout file name

        val signupButton: Button = findViewById(R.id.signUpButton)

        signupButton.setOnClickListener {
            val Intent= Intent(this, HomePageActivity::class.java)
            startActivity(Intent)
        }

        val signButton: Button = findViewById(R.id.signinButton)
        signButton.text = getString(R.string.sign_in)
        signButton.setOnClickListener {

            val editText1 = findViewById<EditText>(R.id.editText1)
            val editText2 = findViewById<EditText>(R.id.editText2)

            val email = editText1.text.toString()
            val password = editText2.text.toString()

            verifyAccount(email, password)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,"no account found", Toast.LENGTH_SHORT).show()
                    val user = task.result?.user
                    // Handle signed-in user

                } else {
                    Toast.makeText(this,"you have successfully entered your account", Toast.LENGTH_SHORT).show()
                    // Sign-in failed
                    // Handle error
                }
            }


        }


    }


}
