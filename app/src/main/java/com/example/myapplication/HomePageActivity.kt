package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityHomePageBinding
import com.google.firebase.auth.FirebaseAuth
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.GravityCompat

class HomePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set OnClickListener for the QR ImageView
        binding.qrView.setOnClickListener {
            onImageViewClick("QR ImageView")

            val Intent= Intent(this, QRScannerActivity::class.java)
            startActivity(Intent)

        }

        // Set OnClickListener for the Credit Card CardView
        binding.creditCardLayoutView.setOnClickListener {
            onCardViewClick("Credit Card CardView")
            val Intent= Intent(this, EnterAmountActivity::class.java)
            startActivity(Intent)
        }

        // Set OnClickListener for the Payment CardView
        binding.financeView.setOnClickListener{
            onCardViewClick("Financial Carview")
        }

        // Set OnClickListener for the Wealth CardView
        binding.paymentsView.setOnClickListener {
            onCardViewClick("Payments CardView")
            val intent = Intent(this, PaymentHistoryActivity::class.java)
            startActivity(intent)
        }

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_my_profile -> {
                    // Handle the My Profile action
                    val intent = Intent(this, MyProfileActivity::class.java)
                    startActivity(intent)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                // Add other cases for different menu items if necessary
                else -> false
            }
        }

        binding.menuView.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.logoutView.setOnClickListener {
            logoutUser()
        }
    }

    private fun onCardViewClick(cardName: String) {
        println("Clicked on $cardName")
    }

    private fun onImageViewClick(imageName: String) {
        println("Clicked on $imageName")
    }
    private fun logoutUser() {
        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut()

        // Redirect to MainActivity (Sign-in Screen)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK  // Clear the activity stack
        startActivity(intent)
    }

}
