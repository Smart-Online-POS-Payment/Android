package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up) // Make sure this matches your layout file name
        val signButton: Button = findViewById(R.id.signinButton2)
        print("****************")
        signButton.setOnClickListener {
            print("****************")


            val editText1 = findViewById<EditText>(R.id.editText11)
            val editText2 = findViewById<EditText>(R.id.editText21)

            val email = editText1.text.toString()
            val password = editText2.text.toString()

            val the_account: Account? = AuthService.sign_up(email, password)

            if (the_account == null) {

                Toast.makeText(this,"unsucceesful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this,"you have successfully signed up", Toast.LENGTH_SHORT).show()
            }
        }

    }
}

