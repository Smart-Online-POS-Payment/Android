package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
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
import java.util.Calendar
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

        setupCardHolderNameField()
        setupExpiryDateField()
        setupCardNumberField()
        val backButton: Button = findViewById(R.id.buttonBack)
        backButton.setOnClickListener {
            finish()
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

    private fun setupCardHolderNameField() {
        val editTextCardHolderName = findViewById<EditText>(R.id.editTextCardHolderName)
        editTextCardHolderName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                val filter = s.toString().filter { it.isLetter() || it.isWhitespace() }
                if (filter != s.toString()) {
                    editTextCardHolderName.setText(filter)
                    editTextCardHolderName.setSelection(filter.length)
                }
            }
        })
    }
    private fun setupCardNumberField() {
        val editTextCardNumber = binding.editTextCardNumber
        editTextCardNumber.addTextChangedListener(object : TextWatcher {
            var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                val formatted = formatCardNumber(s.toString())
                editTextCardNumber.setText(formatted)
                editTextCardNumber.setSelection(formatted.length)

                isFormatting = false
            }

            private fun formatCardNumber(text: String): String {
                return text.replace(" ", "")
                    .chunked(4)
                    .joinToString(" ")
                    .take(19)
            }
        })
    }

    private fun setupExpiryDateField() {
        val editTextExpiryDate = findViewById<EditText>(R.id.editTextExpiryDate)
        editTextExpiryDate.addTextChangedListener(object : TextWatcher {
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
                    editTextExpiryDate.setText(current)
                    editTextExpiryDate.setSelection(if (sel < current.length) sel else current.length)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {}
        })
    }

}