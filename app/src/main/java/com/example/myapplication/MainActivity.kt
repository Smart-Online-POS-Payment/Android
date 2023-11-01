package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Make sure this matches your layout file name

        val signButton: Button = findViewById(R.id.signinButton)
        signButton.text = getString(R.string.sign_in)
        signButton.setOnClickListener {

            val editText1 = findViewById<EditText>(R.id.editText1)
            val editText2 = findViewById<EditText>(R.id.editText2)

            val email = editText1.text.toString()
            val password = editText2.text.toString()

            val firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign-in success
                    Toast.makeText(this, "You have successfully entered your account", Toast.LENGTH_SHORT).show()
                } else {
                    // Sign-in failed
                    Toast.makeText(this, "No account found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
