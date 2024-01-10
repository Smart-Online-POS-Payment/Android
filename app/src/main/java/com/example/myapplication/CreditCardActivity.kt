package com.example.myapplication



// CreditCardActivity.kt

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.model.CardModel
import com.example.myapplication.model.PaymentDetailsModel
import com.example.myapplication.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class CreditCardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var creditCardAdapter: CreditCardAdapter
    private val client = OkHttpClient().newBuilder().build()
    private var cardList = mutableListOf<CardModel>()

    private var paymentAmount: Double = 0.0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_credit_card)

        // Toolbar setup (optional)
        //  val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        // setSupportActionBar(toolbar)
        paymentAmount = intent.getDoubleExtra("EXTRA_AMOUNT", 0.0)
        // Set click listener for the back button


        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerViewCreditCards)
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Replace the following line with your actual list of credit cards
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid =  currentUser!!.uid
        setCards(uid)
        creditCardAdapter = CreditCardAdapter(cardList, this)
        recyclerView.adapter = creditCardAdapter

        val backButton: ImageView = findViewById(R.id.back_button)
        backButton.setOnClickListener { onBackPressed() }

        val buttonMakePayment: Button = findViewById(R.id.btnMakePayment)
        buttonMakePayment.text = "Make the Payment of $paymentAmount"


        buttonMakePayment.setOnClickListener() {
            currentUser.getIdToken(true).addOnSuccessListener { tokenResult ->
                tokenResult.token?.let { creditCardAdapter.getSelectedCreditCard()
                    ?.let { it1 -> addMoney(uid, paymentAmount.toLong(), it , it1) } }
            }
            Handler().postDelayed({
                val intent = Intent(this, HomePageActivity::class.java)
                startActivity(intent)
            }, 1000)
        }


        val addnewcard: Button = findViewById(R.id.addnewcard)
        addnewcard.setOnClickListener {
            val intent = Intent(this@CreditCardActivity, ChooseCardActivity::class.java)
            startActivity(intent)
        }

        val selectedCreditCard = creditCardAdapter.getSelectedCreditCard()

        if (selectedCreditCard != null) {
            // Use the selected credit card information as needed
            val cardNumber = selectedCreditCard.cardNumber
            val expirationDate = selectedCreditCard.expiryDate
            val cardholderName = selectedCreditCard.cardholderName
            val cvvcode=selectedCreditCard.cvvCode

            // Perform actions with the selected credit card...
        }


    }

    // Replace this function with your actual logic to fetch or generate credit card data
    private fun setCards(customerId: String) {
        val request = Request.Builder()
            .url("${Constants.WALLET_URL}/wallet/${customerId}/cards")
            .get()
            .addHeader("Content-Type", "application/json")
            .build()
        client.newCall(request).enqueue(object : Callback{
            @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body
                    if (responseBody != null) {
                        val gson = Gson()
                        val cardListType = object : TypeToken<List<CardModel>>() {}.type
                        val list: List<CardModel> = gson.fromJson(responseBody.string(), cardListType)
                        runOnUiThread {
                            cardList.clear()
                            cardList.addAll(list)
                            creditCardAdapter.notifyDataSetChanged()
                        }
                    }
                } else {
                    Log.e("error->", response.toString())
                    Log.e("Error", "Failed to get credit cards")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error", "Failed to get payments", e)
            }
        })
    }
    private fun addMoney(customerId: String, amount: Long, token: String, cardModel: CardModel){
        val client = OkHttpClient().newBuilder().build()
        val gson = Gson()
        val json = gson.toJson(cardModel)
        val bodyRequest = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json)
        val request = Request.Builder()
            .url("${Constants.WALLET_URL}/wallet/card-payment/$customerId/amount/$amount")
            .put(bodyRequest)
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
