// PaymentHistoryActivity.kt
package com.example.myapplication

import PaymentsAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityPaymentHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.IOException
import java.util.Date

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

        val backButton: Button = findViewById(R.id.buttonBack)
        backButton.setOnClickListener {
            finish()  // Closes the current activity, returning to the previous one in the stack
        }
        // Send request to backend
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser!!.getIdToken(true).addOnSuccessListener { tokenResult ->
            tokenResult.token?.let { getPayments(currentUser.uid, it) }
        }

    //paymentsRecyclerView.adapter = PaymentsAdapter(getPayments("",""))
    }
    private fun getPayments(customerId: String, accessToken: String): ResponseBody? {
        val client = OkHttpClient().newBuilder().build()
        val request = Request.Builder()
            .url("http://192.168.128.54:8083/payment/payment-order/customer/$customerId")
            .get()
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .build()
        val gson = Gson()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body
                    if (responseBody != null) {
                        val json = responseBody.string()
                        Log.i("Response:", json)

                        // Parse the JSON into a List<Payment> using Gson
                        val paymentList: List<Payment> = gson.fromJson(json, object : TypeToken<List<Payment>>() {}.type)
                        runOnUiThread {
                            val paymentsRecyclerView: RecyclerView = findViewById(R.id.paymentsRecyclerView)
                            // Check if the RecyclerView and paymentList are not null
                            paymentsRecyclerView.adapter = paymentList?.let { PaymentsAdapter(it) }
                        }

                    }
                    // Process the responseBody here
                } else {
                    Log.e("Error", "Failed to get payments")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error", "Failed to get payments", e)
            }
        })

        return null // Replace with the actual return value
    }


    data class Payment( val amount: String, val description: String, val date: String)


}
