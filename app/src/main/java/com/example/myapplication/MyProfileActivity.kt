package com.example.myapplication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import java.util.Calendar

class MyProfileActivity : AppCompatActivity() {
    private val tcknValue: Long = tcknEditText.text.toString().toLongOrNull() ?: 0L
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
    val currentUser = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        initializeViews()


        emailEditText.setText(currentUser?.email)
        passwordEditText.setText("********")

        verifyTcknButton.setOnClickListener {
            val tcknValue = tcknEditText.text.toString().toLongOrNull() ?: 0L
            verifyTckn(tcknValue, nameEditText.text.toString(), surnameEditText.text.toString(), emailEditText.text.toString(),
                dobEditText.text.toString(), phoneNumberEditText.text.toString(), openAddressEditText.text.toString(), cityEditText.text.toString())
        }

        changePasswordButton.setOnClickListener {
            // Implementation for changing password
        }

        backButton.setOnClickListener {
            finish() // This will close MyProfileActivity and return to HomePageActivity
        }
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


    private fun verifyTckn(tckn: Long, name: String, surname: String, email: String, dob: String, phoneNumber: String, openAddress: String, city: String) {
        val verifyRequest = TcknVerifyRequest(tckn, name, surname, email, dob, phoneNumber, openAddress, city)
        sendVerificationRequest(verifyRequest)
    }

    private fun sendVerificationRequest(verifyRequest: TcknVerifyRequest) {
        val gson = Gson()
        val jsonRequest = gson.toJson(verifyRequest)
        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsonRequest)
        val cuid = currentUser?.uid
        val request = Request.Builder()
            .url("http://172.20.18.2:8081/verify/customer/$cuid") // Replace with your verification endpoint
            .post(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Verification request failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(applicationContext, "Verification request sent successfully", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(applicationContext, "Verification request failed: ${response.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }


    private fun setupDateField() {
        val setupDateField = findViewById<EditText>(R.id.editTextExpiryDate)
        setupDateField.addTextChangedListener(object : TextWatcher {
            private var current = ""
            private val ddmmyyyy = "DDMMYYYY"
            private val cal = Calendar.getInstance()

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != current) {
                    var clean = s.toString().replace(Regex("[^\\d.]|\\."), "")
                    val cleanC = current.replace(Regex("[^\\d.]|\\."), "")

                    val cl = clean.length
                    var sel = cl
                    var i = 2
                    while (i <= cl && i < 6) {
                        sel++
                        i += 2
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean == cleanC) sel--

                    if (clean.length < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length)
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        var day = Integer.parseInt(clean.substring(0, 2))
                        var mon = Integer.parseInt(clean.substring(2, 4))
                        var year = Integer.parseInt(clean.substring(4, 8))

                        mon = if (mon < 1) 1 else if (mon > 12) 12 else mon
                        cal.set(Calendar.MONTH, mon - 1)
                        year = if (year < 2023) 2023 else if (year > 2100) 2100 else year
                        cal.set(Calendar.YEAR, year)
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, it will crash
                        day = if (day > cal.getActualMaximum(Calendar.DATE)) cal.getActualMaximum(Calendar.DATE) else day
                        clean = String.format("%02d%02d%02d", day, mon, year)
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8))

                    sel = if (sel < 0) 0 else sel
                    current = clean
                    setupDateField.setText(current)
                    setupDateField.setSelection(if (sel < current.length) sel else current.length)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {}
        })
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