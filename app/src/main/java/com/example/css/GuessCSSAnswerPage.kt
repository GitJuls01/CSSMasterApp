package com.example.css

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GuessCSSAnswerPage : AppCompatActivity() {

    private var backPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_guess_cssanswer_page)
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
                    MusicManager.stop()
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


        val backButton = findViewById<ImageButton>(R.id.back_button)
        val nextButton = findViewById<ImageButton>(R.id.gc_next_button)
        val imageView = findViewById<ImageView>(R.id.tq_question_image)
        val feedbackTextView = findViewById<TextView>(R.id.tq_question_feedback)
        val termTextView = findViewById<TextView>(R.id.gc_term)
        val meaningTextView = findViewById<TextView>(R.id.gc_meaning)

        // Get data
        val question = intent.getSerializableExtra("question_data") as? GuessCSSMainGame.Question
        val selectedAnswer = intent.getStringExtra("selected_answer")
        question?.let {
            imageView.setImageResource(it.imageResId)
            termTextView.text = it.correctAnswer
            meaningTextView.text = it.description
            when (selectedAnswer) {
                it.correctAnswer -> {
                    feedbackTextView.text = getString(R.string.correct_answer_feedback, selectedAnswer)
                }
                "You have no answer" -> {
                    feedbackTextView.text = getString(R.string.no_answer_feedback)
                }
                else -> {
                    feedbackTextView.text = getString(R.string.wrong_answer_feedback, selectedAnswer)
                }
            }


        }

        nextButton.setOnClickListener {
            val currentIndex = intent.getIntExtra("questionIndex", 0)
            val intent = Intent(this, GuessCSSMainGame::class.java)
            intent.putExtra("loadNext", true)
            intent.putExtra("questionIndex", currentIndex) // ✅ pass current index
            startActivity(intent)
            finish()
        }

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("openFragment", "games")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            MusicManager.stop()
            finish()

        }
    }

}