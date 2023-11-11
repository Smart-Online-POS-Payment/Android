package com.example.myapplication



import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract. ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.ActivityQrCodeScannerBinding

class QRScannerActivity: AppCompatActivity() {
    private val requestPermissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        isGranted: Boolean ->
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

    private fun setResult(string: String) {
        binding.textResult.text=string


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
            Toast.makeText(context, "Camear permission is required", Toast.LENGTH_SHORT)
        }
        else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }
    private fun initBinding(){
        binding=ActivityQrCodeScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}