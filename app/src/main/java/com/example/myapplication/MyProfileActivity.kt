package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
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

        currentUser?.getIdToken(true)?.addOnSuccessListener { tokenResult ->
            tokenResult.token?.let {
                if (isUserVerified(this)) {
                    loadUserData(it)
                    makeFieldsNonEditable()
                } else {
                    setupButtonListeners(it)
                }
            }
        }

        backButton.setOnClickListener{ onBackPressed() }
    }
    private fun loadUserData(accessToken: String) {
        val request = Request.Builder()
            .url("${Constants.GATEWAY_URL}/verify/customer/${currentUser?.uid}")
            .get()
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Failed to load user data: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                try {
                    val userData = Gson().fromJson(responseBody, UserData::class.java)
                    runOnUiThread {
                        tcknEditText.setText(userData.tckn.toString())
                        nameEditText.setText(userData.firstname)
                        surnameEditText.setText(userData.surname)
                        dobEditText.setText(userData.birthYear.toString())
                        phoneNumberEditText.setText(userData.phoneNumber)
                        openAddressEditText.setText(userData.openAddress)
                        cityEditText.setText(userData.city)
                        emailEditText.setText(currentUser?.email)
                        passwordEditText.setText("********")
                    }
                } catch (e: Exception) {
                    Log.e("MyProfileActivity", "Error parsing user data", e)
                }
            }
        })
    }

    data class UserData(
        val tckn: Long,
        val firstname: String,
        val surname: String,
        val birthYear: Int,
        val phoneNumber: String,
        val openAddress: String,
        val city: String,
        val email: String
    )


    private fun makeFieldsNonEditable() {
        tcknEditText.isEnabled = false
        nameEditText.isEnabled = false
        surnameEditText.isEnabled = false
        dobEditText.isEnabled = false
        phoneNumberEditText.isEnabled = false
        openAddressEditText.isEnabled = false
        cityEditText.isEnabled = false
        emailEditText.isEnabled = false
        passwordEditText.isEnabled = false
        verifyTcknButton.isEnabled = false
        changePasswordButton.isEnabled = false
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
    }

    private fun setupButtonListeners(accessToken: String) {
        verifyTcknButton.setOnClickListener {
            val tcknValue = tcknEditText.text.toString().toLongOrNull() ?: 0L
            verifyTckn(userId = currentUser?.uid!!, tc = tcknValue, name = nameEditText.text.toString(), surname = surnameEditText.text.toString(),
                email = emailEditText.text.toString(),
                phoneNumber = phoneNumberEditText.text.toString(), birthYear = dobEditText.text.toString().toInt(), openAddress = openAddressEditText.text.toString(),
                city = cityEditText.text.toString(), accessToken)
        }

        emailEditText.setText(currentUser?.email)
        passwordEditText.setText("********")

        changePasswordButton.setOnClickListener {
            // Implementation for changing password
        }

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
        city: String,
        accessToken: String
    ) {
        val verifyRequest = TcknVerifyRequest(userId = userId , tc = tc, firstname = name, surname = surname, email = email, phoneNumber = phoneNumber, birthYear = birthYear, openAddress = openAddress, city = city)
        sendVerificationRequest(verifyRequest, accessToken)
    }

    private fun sendVerificationRequest(verifyRequest: TcknVerifyRequest, accessToken: String) {
        val gson = Gson()
        val jsonRequest = gson.toJson(verifyRequest)
        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsonRequest)
        Log.i("aa", "entered")

        val request = Request.Builder()
            .url("${Constants.GATEWAY_URL}/verify")
            .post(requestBody)
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
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

                runOnUiThread {
                    if (response.isSuccessful && isVerified) {
                        Toast.makeText(applicationContext, "Verification successful", Toast.LENGTH_LONG).show()
                        MyProfileActivity.setUserVerified(this@MyProfileActivity, true)
                    } else {
                        Toast.makeText(applicationContext, "Verification failed: ${response.message}", Toast.LENGTH_LONG).show()
                        MyProfileActivity.setUserVerified(this@MyProfileActivity, false)
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
        private const val PREFS_NAME = "MyAppPrefs"
        private const val VERIFIED_KEY = "IsUserVerified"

        fun isUserVerified(context: Context): Boolean {
            val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(VERIFIED_KEY, false)
        }

        fun setUserVerified(context: Context, isVerified: Boolean) {
            val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(VERIFIED_KEY, isVerified).apply()
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
