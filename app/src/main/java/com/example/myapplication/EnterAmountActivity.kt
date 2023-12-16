package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityEnterAmountBinding



class EnterAmountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEnterAmountBinding
    private lateinit var editTextAmount: EditText
    private lateinit var buttonSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterAmountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!MyProfileActivity.isUserVerified(this)) {
            // User not verified, redirect to MyProfileActivity
            val intent = Intent(this, MyProfileActivity::class.java)
            startActivity(intent)
            finish()
            return
        }


        editTextAmount = binding.editTextAmount
        buttonSubmit = binding.buttonSubmit


        buttonSubmit.setOnClickListener {
            handleSubmission()
        }

        val backButton: Button = findViewById(R.id.buttonBack)
        backButton.setOnClickListener {
            finish()
        }


    }

    private fun handleSubmission() {
        val amount = editTextAmount.text.toString()

        val intent = Intent(this, CreditCardActivity::class.java)

        intent.putExtra("EXTRA_AMOUNT", amount)

        startActivity(intent)
    }
}