package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityHomePageBinding



class HomePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set OnClickListener for the QR ImageView
        binding.qrView.setOnClickListener {
            onImageViewClick("QR ImageView")
        }

        // Set OnClickListener for the Credit Card CardView
        binding.creditCardLayoutView.setOnClickListener {
            onCardViewClick("Credit Card CardView")
        }

        // Set OnClickListener for the Payment CardView
        binding.financeView.setOnClickListener{
            onCardViewClick("Financial Carview")
        }

        // Set OnClickListener for the Wealth CardView
        binding.paymentsView.setOnClickListener{
            onCardViewClick("Payments CardView")
        }
    }

    private fun onCardViewClick(cardName: String) {
        println("Clicked on $cardName")
    }

    private fun onImageViewClick(imageName: String) {
        println("Clicked on $imageName")
    }
}
