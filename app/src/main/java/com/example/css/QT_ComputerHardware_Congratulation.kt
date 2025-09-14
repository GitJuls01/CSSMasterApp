package com.example.css

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class QT_ComputerHardware_Congratulation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_qt_computer_hardware_congratulation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        val backButton = findViewById<ImageButton>(R.id.back_button)
//        backButton.setOnClickListener {
//            val intent = Intent(this, QuizTime::class.java)
//            startActivity(intent)
//        }

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("openFragment", "quizzes")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        val leaderboard = findViewById<ImageButton>(R.id.leaderboard_button)
        leaderboard.setOnClickListener {
            val intent = Intent(this, QuizLeaderboard::class.java)
            startActivity(intent)
        }

//        val playAgainButton = findViewById<ImageButton>(R.id.play_again_button)
//        playAgainButton.setOnClickListener {
//            val intent = Intent(this, QuizTime::class.java)
//            startActivity(intent)
//        }

        val playAgainButton = findViewById<ImageButton>(R.id.play_again_button)
        playAgainButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("openFragment", "quizzes")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        val correctCount = intent.getIntExtra("correct_count", 0)
        val totalCount = intent.getIntExtra("total_count", 0)
        val name = intent.getStringExtra("name") ?: "default"

        val nameText = findViewById<TextView>(R.id.student_name)
        val scoreText = findViewById<TextView>(R.id.score_text)

        nameText.text = getString(R.string.name_label2, name)
        scoreText.text = getString(R.string.quiz_hardware_score, correctCount, totalCount)
    }
}