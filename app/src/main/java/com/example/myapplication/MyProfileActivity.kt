package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var backButton: Button
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
            verifyTckn(tcknValue, nameEditText.text.toString(), surnameEditText.text.toString(),
                emailEditText.text.toString(), dobEditText.text.toString(),
                phoneNumberEditText.text.toString(), openAddressEditText.text.toString(),
                cityEditText.text.toString())
        }

        changePasswordButton.setOnClickListener {
            // Implementation for changing password
        }

        backButton.setOnClickListener {
            finish() // Close the activity
        }
    }

    private fun verifyTckn(tckn: Long, name: String, surname: String, email: String, dob: String, phoneNumber: String, openAddress: String, city: String) {
        val verifyRequest = TcknVerifyRequest(tckn, name, surname, email, dob, phoneNumber, openAddress, city)
        sendVerificationRequest(verifyRequest)
    }

    private fun sendVerificationRequest(verifyRequest: TcknVerifyRequest) {
        val gson = Gson()
        val jsonRequest = gson.toJson(verifyRequest)
        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsonRequest)
        val cuid = currentUser?.uid ?: return // If currentUser is null, return early

        val request = Request.Builder()
            .url("http://192.168.1.107:8081/verify/customer/$cuid") // Replace with your verification endpoint
            .post(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Verification request failed0: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
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
        fun isUserVerified(context: Context): Boolean {
            val sharedPrefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            return sharedPrefs.getBoolean("IsUserVerified", false)
        }
    }
}

data class TcknVerifyRequest(
    val tckn: Long,
    val firstname: String,
    val surname: String,
    val dob: String,
    val email: String,
    val phoneNumber: String,
    val openAddress: String,
    val city: String
)
