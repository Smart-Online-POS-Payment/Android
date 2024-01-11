package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityHomePage1Binding
import com.example.myapplication.databinding.ActivityHomePageBinding
import com.example.myapplication.model.PaymentDetailsModel
import com.example.myapplication.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.internal.wait
import org.json.JSONObject
import java.io.IOException

class HomePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomePage1Binding
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePage1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUser?.let {
            isUserVerified(it.uid)
            getBalance(it.uid)
        }

        setupOnClickListeners()
        checkUserVerificationStatus()
        refreshData()
    }

    private fun isUserVerified(customerId: String){
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${Constants.AUTH_URL}/verify/customer/$customerId/is-verified")
            .get()
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBodyString = response.body?.string()
                val isVerified = responseBodyString?.toBoolean() ?: false
                MyProfileActivity.setUserVerified(this@HomePageActivity, isVerified)
                Log.i("Is verified: ", isVerified.toString())
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error", "Failed to get payments", e)
            }
        })
    }

    private fun setupOnClickListeners() {
        binding.qrView.setOnClickListener {
            val intent = Intent(this, QRScannerActivity::class.java)
            startActivity(intent)
        }

        binding.creditCardView.setOnClickListener {
            startActivity(Intent(this, EnterAmountActivity::class.java))
        }

        binding.paymentsView.setOnClickListener {
            startActivity(Intent(this, PaymentHistoryActivity::class.java))
        }

        binding.dashboard.setOnClickListener{
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        binding.walletImage.setOnClickListener {
            refreshData()
        }

        binding.profileIcon.setOnClickListener {
            startActivity(Intent(this, MyProfileActivity::class.java))
        }

        binding.logoutView.setOnClickListener {
            logoutUser()
        }
    }

    private fun checkUserVerificationStatus() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return // If currentUser is null, return early
        val cuid = currentUser.uid

        val request = Request.Builder()
            .url("${Constants.AUTH_URL}/customer/$cuid/is-verified") // Replace with the correct endpoint
            .get()
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Log.e("Verification Check", "Failed to check verification status: ${e.message}")
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
        val userId = currentUser?.uid ?: return // Return early if UID is null
        val client = OkHttpClient.Builder().build()
        val request = Request.Builder()
            .url("${Constants.WALLET_URL}/wallet/$userId")
            .get()
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@HomePageActivity, "Failed to get balance: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("HomePageActivity", "Failed to get balance", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@HomePageActivity, "Error: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                    Log.e("HomePageActivity", "Error response code: ${response.code}")
                    return
                }

                response.body?.use { responseBody ->
                    val jsonObject = JSONObject(responseBody.string())
                    val balance = jsonObject.getDouble("balance")
                    runOnUiThread {
                        binding.walletText.text = "Balance: $balance"
                        Log.e("Updated Balance", "Successful")
                    }
                } ?: runOnUiThread {
                    Toast.makeText(this@HomePageActivity, "No response from server", Toast.LENGTH_SHORT).show()
                    Log.e("HomePageActivity", "Response body is null")
                }
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

    private fun refreshData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            isUserVerified(it.uid)
            getBalance(it.uid)
        }
        // Add any other data refresh logic here
    }

}
