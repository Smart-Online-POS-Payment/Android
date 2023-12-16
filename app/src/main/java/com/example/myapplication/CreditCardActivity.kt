package com.example.myapplication



// CreditCardActivity.kt

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityEnterAmountBinding

class CreditCardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var creditCardAdapter: CreditCardAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_credit_card)

        // Toolbar setup (optional)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerViewCreditCards)
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Replace the following line with your actual list of credit cards
        val creditCardList = generateSampleCreditCards()
        creditCardAdapter = CreditCardAdapter(creditCardList)
        recyclerView.adapter = creditCardAdapter


        val backButton: Button = findViewById(R.id.addnewcard)
        backButton.setOnClickListener {
            val intent = Intent(this@CreditCardActivity, ChooseCardActivity::class.java)
            startActivity(intent)
        }
    }

    // Replace this function with your actual logic to fetch or generate credit card data
    private fun generateSampleCreditCards(): List<CreditCard> {
        return listOf(
            CreditCard("XXXX-XXXX-XXXX-1111"),
            CreditCard("XXXX-XXXX-XXXX-2222"),
            CreditCard("XXXX-XXXX-XXXX-3333")
            // Add more credit cards as needed
        )
    }
}
