package com.example.myapplication

import PaymentsAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityPaymentHistoryBinding
import com.example.myapplication.model.PaymentDetailsModel
import com.example.myapplication.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException
import java.math.BigDecimal
import java.text.SimpleDateFormat

class PaymentHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentHistoryBinding
    private lateinit var paymentsAdapter: PaymentsAdapter
    private var paymentsList = mutableListOf<PaymentDetailsModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!MyProfileActivity.isUserVerified(this)) {
            // User not verified, redirect to MyProfileActivity
            val intent = Intent(this, MyProfileActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        val backButton: ImageView = findViewById(R.id.back_button)
        backButton.setOnClickListener { onBackPressed() }

        setupRecyclerView()
        loadPaymentsFromBackend()
        //loadMockPayments()
    }

    private fun loadMockPayments() {
        // Mock data
        val mockPayments = listOf(
            PaymentDetailsModel("75494", BigDecimal("80.49"), "Mock Payment #58", "Clothing", SimpleDateFormat("yyyy-MM-dd").parse("2023-12-23")),
            PaymentDetailsModel("12677", BigDecimal("194.29"), "Mock Payment #65", "Electronics", SimpleDateFormat("yyyy-MM-dd").parse("2023-12-12")),
            // Add the rest of your mock payments here
        )

        paymentsList.clear()
        paymentsList.addAll(mockPayments)
        paymentsAdapter.notifyDataSetChanged()
    }
    private fun setupRecyclerView() {
        paymentsAdapter = PaymentsAdapter(paymentsList) { payment ->
            requestRefund(payment)
        }
        binding.paymentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@PaymentHistoryActivity)
            adapter = paymentsAdapter
        }
    }

    private fun loadPaymentsFromBackend() {
        // Assume this function retrieves your payments from the backend
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.getIdToken(true)?.addOnSuccessListener { tokenResult ->
            tokenResult.token?.let { getPayments(currentUser.uid, it) }
        }
    }

    private fun getPayments(customerId: String, accessToken: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${Constants.BASE_URL}:8083/payment/payment-order/customer/$customerId")
            .get()
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val json = responseBody.string()
                    val gson = Gson()
                    val paymentListType = object : TypeToken<List<PaymentDetailsModel>>() {}.type
                    val paymentList: List<PaymentDetailsModel> = gson.fromJson(json, paymentListType)
                    runOnUiThread {
                        paymentsList.clear()
                        paymentsList.addAll(paymentList)
                        paymentsAdapter.notifyDataSetChanged()
                    }
                } ?: Log.e("Error", "Response body is null")
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error", "Failed to get payments", e)
            }
        })
    }

    private fun requestRefund(payment: PaymentDetailsModel) {
        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("paymentId", payment.paymentId)  // Use the newly added paymentId field
            .build()

        val request = Request.Builder()
            .url("${Constants.BASE_URL}:8083/refund-request")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@PaymentHistoryActivity, "Refund request failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@PaymentHistoryActivity, "Refund request sent", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@PaymentHistoryActivity, "Failed to request refund", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
