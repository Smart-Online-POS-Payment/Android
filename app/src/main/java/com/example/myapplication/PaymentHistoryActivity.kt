package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityPaymentHistoryBinding
import com.example.myapplication.model.PaymentDetailsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException

class PaymentHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentHistoryBinding
    private lateinit var paymentsAdapter: PaymentsAdapter
    private var paymentsList = mutableListOf<PaymentDetailsModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!isUserVerified()) {
            startActivity(Intent(this, MyProfileActivity::class.java))
            finish()
            return
        }

        binding.buttonBack.setOnClickListener {
            finish() // Return to the previous activity
        }

        setupRecyclerView()
        loadPaymentsFromBackend()
    }

    private fun isUserVerified(): Boolean {
        // Implement the logic to check if the user is verified
        // This could involve checking SharedPreferences or making a network request
        return false
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
            .url("http://192.168.128.54:8083/payment/payment-order/customer/$customerId")
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
        // Implement refund request logic here
        Log.i("Refund Request", "Refund requested for: ${payment.description}")
    }
}
