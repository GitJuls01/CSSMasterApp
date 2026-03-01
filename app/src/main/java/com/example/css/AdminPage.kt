package com.example.css

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AdminPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val logoutButton = findViewById<ImageButton>(R.id.back_button)
        logoutButton.setOnClickListener {
//          showConfirmLogout()
            val intent = Intent(this, ChooseUserLogin::class.java)
            startActivity(intent)
        }

        val viewDetailsButton = findViewById<ImageButton>(R.id.admin_view_details_button)
        viewDetailsButton.setOnClickListener {
            val intent = Intent(this, AdminViewDetails::class.java)
            startActivity(intent)
        }
    }
}