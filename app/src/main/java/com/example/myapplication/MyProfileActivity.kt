package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException

class MyProfileActivity : AppCompatActivity() {
    private lateinit var tcknEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var dobEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var openAddressEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var verifyTcknButton: Button
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var changePasswordButton: Button
    private lateinit var backButton: ImageView
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        initializeViews()
        setupButtonListeners()
    }

    private fun initializeViews() {
        tcknEditText = findViewById(R.id.TCKNEditText)
        nameEditText = findViewById(R.id.nameEditText)
        surnameEditText = findViewById(R.id.surnameEditText)
        dobEditText = findViewById(R.id.dobEditText)
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        openAddressEditText = findViewById(R.id.openAddressEditText)
        cityEditText = findViewById(R.id.cityEditText)
        verifyTcknButton = findViewById(R.id.verifyTCKNButton)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        changePasswordButton = findViewById(R.id.changePasswordButton)
        backButton = findViewById(R.id.backButton)

        emailEditText.setText(currentUser?.email)
        passwordEditText.setText("********")
    }

    private fun setupButtonListeners() {
        verifyTcknButton.setOnClickListener {
            val tcknValue = tcknEditText.text.toString().toLongOrNull() ?: 0L
            verifyTckn(userId = currentUser?.uid!!, tc = tcknValue, name = nameEditText.text.toString(), surname = surnameEditText.text.toString(),
                email = emailEditText.text.toString(),
                phoneNumber = phoneNumberEditText.text.toString(), birthYear = dobEditText.text.toString().toInt(), openAddress = openAddressEditText.text.toString(),
                city = cityEditText.text.toString())
        }

        changePasswordButton.setOnClickListener {
            // Implementation for changing password
        }

        backButton.setOnClickListener { onBackPressed() }
    }

    private fun verifyTckn(
        userId: String,
        tc: Long,
        name: String,
        surname: String,
        email: String,
        phoneNumber: String,
        birthYear: Int,
        openAddress: String,
        city: String
    ) {
        val verifyRequest = TcknVerifyRequest(userId = userId , tc = tc, firstname = name, surname = surname, email = email, phoneNumber = phoneNumber, birthYear = birthYear, openAddress = openAddress, city = city)
        sendVerificationRequest(verifyRequest)
    }

    private fun sendVerificationRequest(verifyRequest: TcknVerifyRequest) {
        val gson = Gson()
        val jsonRequest = gson.toJson(verifyRequest)
        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsonRequest)
        Log.i("aa", "entered")

        val request = Request.Builder()
            .url("${Constants.BASE_URL}:3000/verify")
            .post(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Log.i("aa", "fail")
                    Toast.makeText(applicationContext, "Verification request failed0: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBodyString = response.body?.string()
                val isVerified = responseBodyString?.toBoolean() ?: false
                Log.i("Is verified", isVerified.toString())
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(applicationContext, "Verification request sent successfully", Toast.LENGTH_LONG).show()
                        saveVerificationStatus(true)
                    } else {
                        Toast.makeText(applicationContext, "Verification request failed1: ${response.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun saveVerificationStatus(isVerified: Boolean) {
        val sharedPrefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putBoolean("IsUserVerified", isVerified)
            apply()
        }
    }

    companion object {

        private var userVerified = false

        fun isUserVerified(context: Context): Boolean {
            val sharedPrefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            return sharedPrefs.getBoolean("IsUserVerified", userVerified)
        }

        fun setIsUserVerified(boolean: Boolean){
            userVerified = boolean
        }
    }
}

data class TcknVerifyRequest(
    val userId: String,
    val tc: Long,
    val firstname: String,
    val surname: String,
    val email: String,
    val phoneNumber: String,
    val birthYear: Int,
    val openAddress: String,
    val city: String,
    val country: String = "Turkey"
)
