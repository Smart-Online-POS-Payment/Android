package com.example.myapplication

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotificationsActivity : AppCompatActivity() {

    private lateinit var notifications: List<NotificationModel>
    private lateinit var recyclerView: RecyclerView
    private lateinit var introText: TextView
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        // Initialize notifications (replace with your actual data)
        notifications = listOf(
            NotificationModel("Notification 1"),
            NotificationModel("Notification 2")
            // Add more notifications as needed...
        )

        // Initialize views
        introText = findViewById(R.id.intro_text)
        recyclerView = findViewById(R.id.recycler_view)
        backButton = findViewById(R.id.back_button)

        // Set introductory text
        introText.text = "        Your Previous Notifications"

        // Set click listener for the back button
        backButton.setOnClickListener { onBackPressed() }

        // Initialize RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = NotificationsAdapter(notifications)
        recyclerView.adapter = adapter
    }
}
