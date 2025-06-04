package com.example.css

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ReviewAnswers : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_review_answers)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val resultsContainer = findViewById<LinearLayout>(R.id.results_container)
    
        val results = intent.getSerializableExtra("question_results") as? ArrayList<QT_TeacherQuiz_MainGame.QuestionResult>

        results?.forEachIndexed { index, result ->
            val itemView = layoutInflater.inflate(R.layout.review_answer_item, resultsContainer, false)

            itemView.findViewById<TextView>(R.id.question).text =
                getString(R.string.question_format, index + 1, result.question)

            itemView.findViewById<TextView>(R.id.selected_answer).text =
                getString(R.string.your_answer_format, result.selectedAnswer)

            itemView.findViewById<TextView>(R.id.correct_answer).text =
                getString(R.string.correct_answer_format, result.correctAnswer)


            resultsContainer.addView(itemView)
        }

        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            startActivity(Intent(this, QuizTime::class.java))
        }

        findViewById<ImageButton>(R.id.finish_button).setOnClickListener {
            startActivity(Intent(this, QuizTime::class.java))
        }
    }
}
