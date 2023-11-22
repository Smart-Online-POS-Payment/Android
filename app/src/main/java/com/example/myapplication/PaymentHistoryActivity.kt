// PaymentHistoryActivity.kt
package com.example.myapplication

import PaymentsAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityPaymentHistoryBinding

class PaymentHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize RecyclerView with a LinearLayoutManager and an adapter
        binding.paymentsRecyclerView.layoutManager = LinearLayoutManager(this)
        // Here, initialize and set your adapter with the list of payments
        val paymentsRecyclerView: RecyclerView = findViewById(R.id.paymentsRecyclerView)
        paymentsRecyclerView.layoutManager = LinearLayoutManager(this)
        paymentsRecyclerView.adapter = PaymentsAdapter(generateMockPayments())
    }

    data class Payment(val explanation: String, val amount: String)
    private fun generateMockPayments(): List<Payment> {
        return listOf(
            Payment("Payment 1", "$10"),
            Payment("Payment 2", "$20"),
            Payment("Payment 3", "$30"),
        )
    }

}
