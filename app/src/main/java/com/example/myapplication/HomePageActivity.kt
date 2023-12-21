package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityHomePageBinding
import com.example.myapplication.model.PaymentDetailsModel
import com.example.myapplication.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
        currentUser?.let {
            isUserVerified(it.uid)
            getBalance(it.uid)
        }

        setupOnClickListeners()
        checkUserVerificationStatus()
    }

    private fun isUserVerified(customerId: String){
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${Constants.BASE_URL}:8081/verify/customer/$customerId/is-verified")
            .get()
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBodyString = response.body?.string()
                val isVerified = responseBodyString?.toBoolean() ?: false
                MyProfileActivity.setIsUserVerified(isVerified)
                Log.i("Is verified: ", isVerified.toString())
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error", "Failed to get payments", e)
            }
        })
    }

    private fun setupOnClickListeners() {
        binding.qrView.setOnClickListener {
            startActivity(Intent(this, QRScannerActivity::class.java))
        }

        binding.creditCardLayoutView.setOnClickListener {
            startActivity(Intent(this, CreditCardActivity::class.java))
        }

        binding.paymentsView.setOnClickListener {
            startActivity(Intent(this, PaymentHistoryActivity::class.java))
        }

        binding.profileIcon.setOnClickListener {
            startActivity(Intent(this, MyProfileActivity::class.java))
        }

        binding.logoutView.setOnClickListener {
            logoutUser()
        }
        binding.notificationView.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))

        }
    }

    private fun checkUserVerificationStatus() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return // If currentUser is null, return early
        val cuid = currentUser.uid

        val request = Request.Builder()
            .url("http://192.168.1.107:8081/customer/$cuid/is-verified") // Replace with the correct endpoint
            .get()
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Log.e("Verification Check", "Failed to check verification status: ${e.message}")
                    // Handle failure case, such as displaying an error message
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val isVerified = response.body?.string().toBoolean()
                    runOnUiThread {
                        if (isVerified) {
                            // User is verified, proceed as normal
                        } else {
                            // User is not verified, redirect to MyProfileActivity for verification
                            val intent = Intent(this@HomePageActivity, MyProfileActivity::class.java)
                            startActivity(intent)
                            finish() // Optionally finish the current activity
                        }
                    }
                } else {
                    runOnUiThread {
                        Log.e("Verification Check", "Failed to check verification status: ${response.message}")
                        // Handle error response
                    }
                }
            }
        })
    }

    private fun getBalance(customerId: String) {
        val client = OkHttpClient.Builder().build()
        val request = Request.Builder()
            .url("http://192.168.1.107:8070/wallet/$customerId")
            .get()
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HomePageActivity", "Failed to get balance", e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val jsonObject = JSONObject(responseBody.string())
                    val balance = jsonObject.getDouble("balance")
                    runOnUiThread {
                        binding.walletTextView.text = "Balance: $balance"
                    }
                } ?: Log.e("HomePageActivity", "Response body is null")
            }
        })
    }

    private fun logoutUser() {
        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut()

        // Reset verification status
        //resetVerificationStatus()

        // Redirect to MainActivity (Sign-in Screen)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // This line is added to close the current activity
    }

    private fun resetVerificationStatus() {
        val sharedPrefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().apply {
            putBoolean("IsUserVerified", false)
            apply()
        }
    }

}
