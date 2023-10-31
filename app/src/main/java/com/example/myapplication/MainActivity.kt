package com.example.myapplication

import android.R
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val signButton: Button = findViewById(R.id.signinButton)
        signButton.text="Sign in"
        signButton.setOnClickListener {

            val editText1 = findViewById<EditText>(R.id.editText1)
            val editText2 = findViewById<EditText>(R.id.editText2)

            val text1 = editText1.text.toString()
            val text2 = editText2.text.toString()

            val the_account: Account? =AuthService.verifyAccount(email, password);

            if (the_account == null) {
                Toast.makeText(this,"no account found", Toast.LENGTH_SHORT).show()
                // The value is null
            } else {
                Toast.makeText(this,"you have succesffully entered your account", Toast.LENGTH_SHORT).show()

            }


        }

    }

}