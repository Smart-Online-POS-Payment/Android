package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.CategorizedPaymentsAdapter
import com.example.myapplication.adapter.LastMonthPaymentsAdapter
import com.example.myapplication.databinding.ActivityDashboardBinding
import com.example.myapplication.model.PaymentDetailsModel
import com.example.myapplication.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private var paymentsList = mutableListOf<PaymentDetailsModel>()

    private lateinit var categorizedPaymentsAdapter: CategorizedPaymentsAdapter
    private lateinit var lastMonthPaymentsAdapter: LastMonthPaymentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerViews()
        loadPaymentsFromBackend()
    }

    private fun setupRecyclerViews() {
        categorizedPaymentsAdapter = CategorizedPaymentsAdapter(mapOf())
        lastMonthPaymentsAdapter = LastMonthPaymentsAdapter(listOf())

        binding.categorizedPaymentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity)
            adapter = categorizedPaymentsAdapter
        }

        binding.lastMonthsPaymentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity)
            adapter = lastMonthPaymentsAdapter
        }
    }

    private fun loadPaymentsFromBackend() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.getIdToken(true)?.addOnSuccessListener { tokenResult ->
            tokenResult.token?.let { getPayments(currentUser.uid, it) }
        }
    }

    private fun getPayments(customerId: String, accessToken: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${Constants.BASE_URL}/payment/payment-order/customer/$customerId")
            .get()
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { json ->
                        val gson = Gson()
                        val type = object : TypeToken<List<PaymentDetailsModel>>() {}.type
                        paymentsList = gson.fromJson(json, type)

                        processAndDisplayPayments()
                    }
                } else {
                    Log.e("DashboardActivity", "Failed to fetch payments: ${response.message}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("DashboardActivity", "Network error when fetching payments", e)
            }
        })
    }

    private fun processAndDisplayPayments() {
        val categorizedPayments = paymentsList.groupBy { it.category }
        val lastMonth = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }.time
        val lastMonthPayments = paymentsList.filter { it.date.after(lastMonth) }

        runOnUiThread {
            categorizedPaymentsAdapter.updateData(categorizedPayments)
            lastMonthPaymentsAdapter.updateData(lastMonthPayments)
        }
    }
}
