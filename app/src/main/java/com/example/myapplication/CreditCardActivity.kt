package com.example.myapplication



// CreditCardActivity.kt

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityEnterAmountBinding
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class CreditCardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var creditCardAdapter: CreditCardAdapter

    //val paymentAmount = intent.getDoubleExtra("paymentAmount", 0.0)
    val paymentAmount=10



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_credit_card)

        // Toolbar setup (optional)
        //  val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        // setSupportActionBar(toolbar)

        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerViewCreditCards)
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Replace the following line with your actual list of credit cards
        val creditCardList = generateSampleCreditCards()
        creditCardAdapter = CreditCardAdapter(creditCardList, this)
        recyclerView.adapter = creditCardAdapter

        val buttonMakePayment: Button = findViewById(R.id.btnMakePayment)
        buttonMakePayment.text = "Make the Payment of "+paymentAmount.toString()

        val currentUser = FirebaseAuth.getInstance().currentUser

        buttonMakePayment.setOnClickListener() {
            currentUser!!.getIdToken(true).addOnSuccessListener { tokenResult ->
                tokenResult.token?.let { addMoney(currentUser.uid, paymentAmount!!.toLong(), it) }
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
            val expirationDate = selectedCreditCard.expirationDate
            val cardholderName = selectedCreditCard.cardholderName
            val cvvcode=selectedCreditCard.CVVcode

            // Perform actions with the selected credit card...
        }

    }

    // Replace this function with your actual logic to fetch or generate credit card data
    private fun generateSampleCreditCards(): List<CreditCard> {
        return listOf(
            CreditCard("XXXX-XXXX-XXXX-1111", "2023", "Kaan","1111"),
            CreditCard("XXXX-XXXX-XXXX-2222", "2023", "Kaan","1111"),
            CreditCard("XXXX-XXXX-XXXX-3333", "2023", "Kaan","1111")
            // Add more credit cards as needed
        )
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
