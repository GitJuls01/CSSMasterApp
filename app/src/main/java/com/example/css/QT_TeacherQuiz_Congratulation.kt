package com.example.css

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class QT_TeacherQuiz_Congratulation : AppCompatActivity() {

    private var backPressedOnce = false

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_qt_teacher_quiz_congratulation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // Register the back press callback to handle back button presses
        val backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedOnce) {
                    // Exit the app
                    finishAffinity() // Properly exits the app from this point
                } else {
                    backPressedOnce = true
                    // Show a Toast message
                    Toast.makeText(applicationContext, "Double click to exit the app", Toast.LENGTH_SHORT).show()

                    // Reset backPressedOnce flag after 2 seconds
                    android.os.Handler().postDelayed({
                        backPressedOnce = false
                    }, 2000) // Reset after 2 seconds
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        val correctCount = intent.getIntExtra("correct_count", 0)
        val totalCount = intent.getIntExtra("total_count", 0)
        val percentage = intent.getIntExtra("percentage", 0)

        val summaryText = findViewById<TextView>(R.id.summary_text)
        val percentageText = findViewById<TextView>(R.id.score_text)
        summaryText.text = getString(R.string.quiz_summary, correctCount, totalCount)
        percentageText.text = getString(R.string.quiz_percentage, percentage)

        val backButton = findViewById<ImageButton>(R.id.back_button)
        val nextQuizBtn = findViewById<ImageButton>(R.id.btn_next)
        val reviewAnswerBtn = findViewById<ImageButton>(R.id.btn_review)

        val results = intent.getParcelableArrayListExtra(
            "question_results",
            QT_TeacherQuiz_MainGame.QuestionResult::class.java
        )

        backButton.setOnClickListener {
            val intent = Intent(this, QT_TeacherQuiz::class.java)
            startActivity(intent)
            finish()
        }
        nextQuizBtn.setOnClickListener {
            val intent = Intent(this, QT_TeacherQuiz::class.java)
            startActivity(intent)
            finish()
        }

        reviewAnswerBtn.setOnClickListener {
            val intent = Intent(this, ReviewAnswers::class.java)
            intent.putParcelableArrayListExtra("question_results", results)
            startActivity(intent)
            finish()
        }
    }
}