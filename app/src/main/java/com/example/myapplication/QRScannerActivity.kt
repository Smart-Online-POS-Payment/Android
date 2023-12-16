package com.example.myapplication



import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract. ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.example.myapplication.databinding.ActivityQrCodeScannerBinding
import com.example.myapplication.model.PaymentDetailsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException


class QRScannerActivity: AppCompatActivity() {
    private val requestPermissionLauncher= this.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            showCamera()

        }
        else {

        }
    }
    private val scanLauncher =
        registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            run {
                if (result.contents == null) {
                    Toast.makeText(  this,  "Cancelled", Toast.LENGTH_SHORT).show() }
                else {
                    setResult(result.contents)
                }
            }
        }
    private lateinit var binding: ActivityQrCodeScannerBinding

    private fun setResult(qr: String) {
        binding.textResult.text=qr
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser!!.getIdToken(true).addOnSuccessListener { tokenResult ->
            tokenResult.token?.let { getPaymentRequest(qr, it) }
        }
    }
    private fun showCamera() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan QR code")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(false)
        scanLauncher.launch(options)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isUserVerified()) {
            startActivity(Intent(this, MyProfileActivity::class.java))
            finish()
            return
        }
        initBinding()
        initViews()

        val backButton: Button = findViewById(R.id.buttonBack)
        backButton.setOnClickListener {
            finish()  // Closes the current activity, returning to the previous one in the stack
        }
    }

    private fun isUserVerified(): Boolean {
        // Implement the logic to check if the user is verified
        // This could involve checking SharedPreferences or making a network request
        return false
    }

    private fun initViews() {
        binding.fab.setOnClickListener {
            checkPermissionCamera(this)
        }

    }
    private fun checkPermissionCamera(context: Context) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED) {
            showCamera()

        }
        else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT)
        }
        else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }
    private fun initBinding(){
        binding=ActivityQrCodeScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    private fun getPaymentRequest(qr: String, accessToken: String): ResponseBody? {
        val client = OkHttpClient().newBuilder().build()
        val request = Request.Builder()
            .url("http://192.168.128.54:8083/payment/payment-request/customer/$qr")
            .get()
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .build()
        val gson = Gson()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val json = response.body.toString()
                    Log.i("Response:", json)
                    val paymentDetails: PaymentDetailsModel = gson.fromJson(json, object : TypeToken<PaymentDetailsModel>() {}.type)
                    showSelectionDialog(paymentDetails, qr)
                } else {
                    Log.e("Error", "Failed to get payments")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error", "Failed to get payments", e)
            }
        })

        return null
    }
    private fun showSelectionDialog(paymentDetails: PaymentDetailsModel, qr_code: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Payment Needs Confirmation")


        val amount1=paymentDetails.amount
        val description1=paymentDetails.description
        val date1=paymentDetails.date

        val formattedMessage = SpannableString("Date: $date1\nDescription: $description1\nAmount: $amount1")


        formattedMessage.setSpan(StyleSpan(Typeface.BOLD), 0, 0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        formattedMessage.setSpan(StyleSpan(Typeface.BOLD), 0, 0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        formattedMessage.setSpan(StyleSpan(Typeface.BOLD), 0, formattedMessage.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        builder.setMessage(formattedMessage)


        builder.setPositiveButton("Confirm") { dialogInterface, _ ->

            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser!!.getIdToken(true).addOnSuccessListener { tokenResult ->
                tokenResult.token?.let { verify_the_payment(qr_code, it) }
            }

            dialogInterface.dismiss()
        }


        builder.setNegativeButton("Cancel") { dialogInterface, _ ->

            dialogInterface.dismiss() // Close the dialog
        }


        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun verify_the_payment(qr_code: String, accessToken: String): ResponseBody? {
        val cont=this

        val client = OkHttpClient().newBuilder().build()
        val request = Request.Builder()
            .url("http://192.168.128.54:8083/payment/payment-order/customer/$qr_code")
            .get()
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody: ResponseBody? = response.body
                    if (responseBody != null) {
                        Log.i("Response:", responseBody.string())
                        showMessage(cont, "payment is succesfull")


                    }
                    // Process the responseBody here
                } else {
                    Log.e("Error", "Failed to get payments")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error", "Failed to get payments", e)
            }
        })

        return null // Replace with the actual return value
    }
    private fun showMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    data class PaymentRequest(val amount: String, val description: String, val date: String)
}
