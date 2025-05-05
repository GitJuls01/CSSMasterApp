package com.example.css

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TutorialsHardware : BottomNav() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tutorials_hardware)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton = findViewById<ImageButton>(R.id.back_button)

        // Set OnClickListener to navigate to TutorialsHardware
        backButton.setOnClickListener {
            finish() // Go back to the previous screen
        }

        val courseType = intent.getStringExtra("course_type")
        val hardwareTextView = findViewById<TextView>(R.id.computer_hardware)

        if (courseType == "hardware") {
            hardwareTextView.text = "Computer Hardware and Components"
        } else if (courseType == "software") {
            hardwareTextView.text = "Computer Software and Components"
        }

        val tutorVideoCard = findViewById<ImageButton>(R.id.tutor_video_card)
        tutorVideoCard.setOnClickListener {
            val intent = Intent(this, TutorialsVideoPreview::class.java)
            intent.putExtra("course_type", courseType) // pass the same info
            startActivity(intent)
        }

    }

    override fun getLayoutResourceId(): Int = R.layout.activity_tutorials_hardware

}