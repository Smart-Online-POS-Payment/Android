package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.example.myapplication.model.CardModel
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
import com.example.myapplication.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import retrofit2.http.Body
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

        setupCardHolderNameField()
        setupExpiryDateField()
        setupCardNumberField()
        binding.buttonAddCard.setOnClickListener {
            val cardHolderName = binding.editTextCardHolderName.text.toString()
            val cardNumber = binding.editTextCardNumber.text.toString()
            val cvv = binding.editTextCVV.text.toString()
            val expiryDate = binding.editTextExpiryDate.text.toString()
            val currentUser = FirebaseAuth.getInstance().currentUser
            addCard(currentUser?.uid, CardModel(cardHolderName, cardNumber, cvv, expiryDate))
        }
        val backButton: Button = findViewById(R.id.buttonBack)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun addCard(uid: String?, cardModel: CardModel) {
        val client = OkHttpClient()
        val gson = Gson()
        val json = gson.toJson(cardModel)

        val bodyRequest = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json)
        val request = Request.Builder()
            .url("${Constants.WALLET_URL}/wallet/${uid}/cards")
            .post(bodyRequest)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {

                    val responseBodyString = response.body?.string()
                    val isVerified = responseBodyString?.toBoolean() ?: false
                    Log.i("AddCard", "Is verified: $isVerified")
                } else {
                    Log.e("AddCard", "Response not successful: ${response.message}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("AddCard", "Failed to add card", e)
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

                editTextCardNumber.setText(s.toString())
                editTextCardNumber.setSelection(s.toString().length)

                isFormatting = false
            }

        })
    }

    private fun setupExpiryDateField() {
        val editTextExpiryDate = findViewById<EditText>(R.id.editTextExpiryDate)
        editTextExpiryDate.addTextChangedListener(object : TextWatcher {
            private var current = ""
            private val mmyyyy = "MMYYYY"
            private val cal = Calendar.getInstance()

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != current) {
                    var clean = s.toString().replace(Regex("[^\\d.]|\\."), "")
                    val cleanC = current.replace(Regex("[^\\d.]|\\."), "")

                    val cl = clean.length
                    var sel = cl
                    var i = 2
                    while (i <= cl && i < 4) {
                        sel++
                        i += 2
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean == cleanC) sel--

                    if (clean.length < 8) {
                        clean = clean + mmyyyy.substring(clean.length)
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        var mon = Integer.parseInt(clean.substring(2, 4))
                        var year = Integer.parseInt(clean.substring(4, 8))

                        mon = if (mon < 1) 1 else if (mon > 12) 12 else mon
                        cal.set(Calendar.MONTH, mon - 1)
                        year = if (year < 2023) 2023 else if (year > 2100) 2100 else year
                        cal.set(Calendar.YEAR, year)
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, it will crash
                        clean = String.format("%02d%02d", mon, year)
                    }

                    clean = String.format("%s/%s", clean.substring(0, 2),
                        clean.substring(2, 6))

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