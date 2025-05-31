package com.example.css

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView

class QT_ComputerHardware_MainGame : AppCompatActivity() {

    private lateinit var correctWrongAnswerPopup: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_qt_computer_hardware_main_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            finish() // Go back to the previous screen
        }

        correctWrongAnswerPopup = findViewById(R.id.correctWrongAnswerPopup)
        val choiceA = findViewById<FrameLayout>(R.id.choice_a_container)
        val choiceB = findViewById<FrameLayout>(R.id.choice_b_container)
        val choiceC = findViewById<FrameLayout>(R.id.choice_c_container)
        val choiceD = findViewById<FrameLayout>(R.id.choice_d_container)

        // Apply click listeners
        val choices = listOf(choiceA, choiceB, choiceC, choiceD)
        choices.forEach { choice ->
            choice.setOnClickListener {
                showPopupTemporarily()
            }
        }
    }

    private fun showPopupTemporarily() {
        correctWrongAnswerPopup.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            correctWrongAnswerPopup.visibility = View.GONE

            // Navigate to congratulation page after delay
            val intent = Intent(this, QT_ComputerHardware_Congratulation::class.java)
            startActivity(intent)
            // Optional: finish current activity
            // finish()
        }, 1500) // 1.5 seconds
    }

}
