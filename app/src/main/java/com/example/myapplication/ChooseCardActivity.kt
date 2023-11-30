package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityChooseCardBinding
import com.example.myapplication.databinding.ActivityEnterAmountBinding

class ChooseCardActivity: AppCompatActivity() {

    private lateinit var binding: ActivityChooseCardBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseCardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val amount = intent.getStringExtra("EXTRA_AMOUNT")
        binding.textViewFormattedAmount.text = "Add $amount to:"


        val cardHolderName = binding.editTextCardHolderName.text.toString()
        val cardNumber = binding.editTextCardNumber.text.toString()
        val cvv = binding.editTextCVV.text.toString()
        val expiryDate = binding.editTextExpiryDate.text.toString()


    }
}