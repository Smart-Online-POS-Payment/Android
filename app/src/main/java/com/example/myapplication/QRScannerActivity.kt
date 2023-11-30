package com.example.myapplication



import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
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
import com.google.firebase.auth.FirebaseAuth
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
        initBinding()
        initViews()

        val backButton: Button = findViewById(R.id.buttonBack)
        backButton.setOnClickListener {
            finish()  // Closes the current activity, returning to the previous one in the stack
        }
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
            .url("http://172.21.144.238:8083/payment/payment-request/customer/$qr")
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
                        showSelectionDialog(responseBody.string(),qr)








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
    private fun showSelectionDialog(input: String, qr_code: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select an option")
        builder.setMessage(input)


        builder.setPositiveButton("Yes") { dialogInterface, _ ->

            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser!!.getIdToken(true).addOnSuccessListener { tokenResult ->
                tokenResult.token?.let { verify_the_payment(qr_code, it) }
            }

            dialogInterface.dismiss()
        }


        builder.setNegativeButton("No") { dialogInterface, _ ->

            dialogInterface.dismiss() // Close the dialog
        }


        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun verify_the_payment(qr_code: String, accessToken: String): ResponseBody? {
        val cont=this

        val client = OkHttpClient().newBuilder().build()
        val request = Request.Builder()
            .url("http://172.21.144.238:8083/payment/payment-order/customer/$qr_code")
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
}
