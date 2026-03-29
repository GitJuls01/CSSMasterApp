package com.example.css

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.FrameLayout
import android.widget.ImageButton

class AdminHomePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_home_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find FrameLayouts
        val cardApproval = findViewById<FrameLayout>(R.id.card_approval)
        val cardQuiz = findViewById<FrameLayout>(R.id.card_quiz)
        val cardLeaderboard = findViewById<FrameLayout>(R.id.card_leaderboard)
        val settingsButton = findViewById<ImageButton>(R.id.setting_button)

        settingsButton.setOnClickListener {
            val intent = Intent(this, TeacherAccountSettings::class.java)
            startActivity(intent)
        }

        // Set click listeners
        cardApproval.setOnClickListener {
            val intent = Intent(this, AdminApprovalPage::class.java)
            startActivity(intent)
        }

        cardQuiz.setOnClickListener {
            val intent = Intent(this, TeacherDashboard::class.java)
            startActivity(intent)
        }

        cardLeaderboard.setOnClickListener {
            val intent = Intent(this, AdminStudentDashboard::class.java)
            startActivity(intent)
        }
    }
}