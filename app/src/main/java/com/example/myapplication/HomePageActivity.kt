package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityHomePageBinding
import com.google.firebase.auth.FirebaseAuth

import android.view.MenuItem
import android.view.View

import android.widget.ImageView
import android.widget.PopupMenu

import androidx.core.view.GravityCompat
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
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
        // Set OnClickListener for the QR ImageView
        binding.qrView.setOnClickListener {
            onImageViewClick("QR ImageView")

            val Intent= Intent(this, QRScannerActivity::class.java)
            startActivity(Intent)

        }

        // Set OnClickListener for the Credit Card CardView
        binding.creditCardLayoutView.setOnClickListener {
            onCardViewClick("Credit Card CardView")
            val Intent= Intent(this, EnterAmountActivity::class.java)
            startActivity(Intent)
        }

        // Set OnClickListener for the Payment CardView

        // Set OnClickListener for the Wealth CardView
        binding.paymentsView.setOnClickListener {
            onCardViewClick("Payments CardView")
            val intent = Intent(this, PaymentHistoryActivity::class.java)
            startActivity(intent)
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

    private fun getBalance(customerId: String){
        val client = OkHttpClient().newBuilder().build()
        val request = Request.Builder()
            .url("http://192.168.128.54:8070/wallet/$customerId")
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
                            Log.i("Balance:", balance.toString())
                            binding.walletTextView.text = "Balance: $balance"

                            // Now you can use the 'balance' variable as needed.
                        } catch (e: Exception) {
                            Log.e("Error", "Failed to parse JSON", e)
                        }
                    }
                } else {
                    Log.e("Error", "Failed to get payments")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error", "Failed to get payments", e)
            }
        })
    }

    private fun onCardViewClick(cardName: String) {
        println("Clicked on $cardName")
    }

    private fun onImageViewClick(imageName: String) {
        println("Clicked on $imageName")
    }
    private fun logoutUser() {
        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut()

        // Redirect to MainActivity (Sign-in Screen)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  // Clear the activity stack
        startActivity(intent)
    }

}
