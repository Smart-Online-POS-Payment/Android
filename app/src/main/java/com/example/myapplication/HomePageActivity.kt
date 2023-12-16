package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityHomePageBinding
import com.google.firebase.auth.FirebaseAuth
import okhttp3.*
import org.json.JSONObject

import java.io.IOException

class HomePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = FirebaseAuth.getInstance().currentUser
        getBalance(currentUser!!.uid)

        setupOnClickListeners()
    }

    private fun setupOnClickListeners() {
        binding.qrView.setOnClickListener {
            startActivity(Intent(this, QRScannerActivity::class.java))
        }

        binding.creditCardLayoutView.setOnClickListener {
            startActivity(Intent(this, EnterAmountActivity::class.java))
        }

        binding.paymentsView.setOnClickListener {
            startActivity(Intent(this, PaymentHistoryActivity::class.java))
        }

        val profileIcon = findViewById<ImageView>(R.id.profileIcon)
        profileIcon.setOnClickListener {
            val intent = Intent(this, MyProfileActivity::class.java)
            startActivity(intent)
        }

        binding.logoutView.setOnClickListener {
            logoutUser()
        }
    }

    private fun getBalance(customerId: String) {
        val client = OkHttpClient().newBuilder().build()
        val request = Request.Builder()
            .url("http://192.168.1.107:8070/wallet/$customerId")
            .get()
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body
                    if (responseBody != null) {
                        try {
                            val jsonObject = JSONObject(responseBody.string())
                            val balance = jsonObject.getDouble("balance")
                            runOnUiThread {
                                binding.walletTextView.text = "Balance: $balance"
                            }
                        } catch (e: Exception) {
                            Log.e("Error", "Failed to parse JSON", e)
                        }
                    }
                } else {
                    Log.e("Error", "Failed to get balance")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error", "Failed to get balance", e)
            }
        })
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // This line is added to close the current activity
    }
}
