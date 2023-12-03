package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityChooseCardBinding
import com.example.myapplication.databinding.ActivityEnterAmountBinding
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import kotlin.math.log

class ChooseCardActivity: AppCompatActivity() {

    private lateinit var binding: ActivityChooseCardBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseCardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val amount = intent.getStringExtra("EXTRA_AMOUNT")
        binding.textViewFormattedAmount.text = "Add $amount to:"


        val cardHolderName = binding.editTextCardHolderName.text.toString()
        val cardNumber = binding.editTextCardNumber.text.toString()
        val cvv = binding.editTextCVV.text.toString()
        val expiryDate = binding.editTextExpiryDate.text.toString()
        val currentUser = FirebaseAuth.getInstance().currentUser
        binding.buttonAddAmount.setOnClickListener {
            currentUser!!.getIdToken(true).addOnSuccessListener { tokenResult ->
                tokenResult.token?.let { addMoney(currentUser.uid, amount!!.toLong(), it) }
            }
            Handler().postDelayed({
                val intent = Intent(this, HomePageActivity::class.java)
                startActivity(intent)
            }, 1000)
        }
    }

    private fun addMoney(customerId: String, amount: Long, token: String){
        val client = OkHttpClient().newBuilder().build()
        val request = Request.Builder()
            .url("http://192.168.128.54:8082/wallet/$customerId/amount/$amount")
            .put(RequestBody.create(null, ByteArray(0))) // Empty request body
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body
                    if (responseBody != null) {
                        Log.i("Body->",responseBody.string())
                    }
                } else {
                    Log.e("error->", response.toString())
                    Log.e("Error", "Failed to get payments")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error", "Failed to get payments", e)
            }
        })
    }
}