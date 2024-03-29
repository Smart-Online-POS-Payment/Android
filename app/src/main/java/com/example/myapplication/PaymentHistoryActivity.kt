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
            val intent = Intent(this, MyProfileActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        val backButton: ImageView = findViewById(R.id.back_button)
        backButton.setOnClickListener { onBackPressed() }

        setupRecyclerView()
        loadPaymentsFromBackend()
    }

    private fun setupRecyclerView() {
        paymentsAdapter = PaymentsAdapter(paymentsList) { payment ->
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.getIdToken(true)?.addOnSuccessListener { tokenResult ->
                tokenResult.token?.let { requestRefund(payment, it) }
            }
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
            .url("${Constants.GATEWAY_URL}/payment/payment-order/customer/$customerId")
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

    private fun requestRefund(payment: PaymentDetailsModel, accessToken: String) {
        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .build()

        val request = Request.Builder()
            .url("${Constants.GATEWAY_URL}/payment/refund/${payment.orderId}")
            .post(requestBody)
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
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
