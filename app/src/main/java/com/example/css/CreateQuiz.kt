package com.example.css

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CreateQuiz : AppCompatActivity() {

    private var questionCount = 1
    private lateinit var firestore: FirebaseFirestore

    // Track all question blocks
    private val questionBlocks = mutableListOf<List<EditText>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_quiz)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firestore = FirebaseFirestore.getInstance()

        // Register the first question block (already in XML)
        val question1 = findViewById<EditText>(R.id.editTextQuestion1)
        val correct1 = findViewById<EditText>(R.id.editTextCorrectAnswer1)
        val wrong1 = findViewById<EditText>(R.id.editTextWrongAnswer1_1)
        val wrong2 = findViewById<EditText>(R.id.editTextWrongAnswer1_2)
        val wrong3 = findViewById<EditText>(R.id.editTextWrongAnswer1_3)

        questionBlocks.add(listOf(question1, correct1, wrong1, wrong2, wrong3))

        val addQuestionBtn = findViewById<ImageButton>(R.id.btnAddQuestion)
        addQuestionBtn.setOnClickListener {
            addQuestionBlock()
        }

        val saveBtn = findViewById<ImageButton>(R.id.btnSubmitQuiz)
        saveBtn.setOnClickListener {
            saveQuizToFirestore()
        }
    }

    private fun addQuestionBlock() {
        questionCount++

        val container = findViewById<LinearLayout>(R.id.questionContainer)

        val questionBlock = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 0, 0, dpToPx(12))  // paddingBottom = 12dp
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(16)  // layout_marginBottom = 16dp
            }
        }

        val question = EditText(this).apply {
            hint = "Question $questionCount"
            minHeight = dpToPx(48)
            background = ContextCompat.getDrawable(this@CreateQuiz, R.drawable.teacher_input_container)
        }

        val correct = EditText(this).apply {
            hint = "Correct Answer"
            minHeight = dpToPx(48)
            background = ContextCompat.getDrawable(this@CreateQuiz, R.drawable.teacher_input_container)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(8)
            }
        }

        val wrong1 = EditText(this).apply {
            hint = "Wrong Answer 1"
            minHeight = dpToPx(48)
            background = ContextCompat.getDrawable(this@CreateQuiz, R.drawable.teacher_input_container)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(4)
            }
        }

        val wrong2 = EditText(this).apply {
            hint = "Wrong Answer 2"
            minHeight = dpToPx(48)
            background = ContextCompat.getDrawable(this@CreateQuiz, R.drawable.teacher_input_container)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(4)
            }
        }

        val wrong3 = EditText(this).apply {
            hint = "Wrong Answer 3"
            minHeight = dpToPx(48)
            background = ContextCompat.getDrawable(this@CreateQuiz, R.drawable.teacher_input_container)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(4)
            }
        }

        // Add them to block and register
        questionBlock.addView(question)
        questionBlock.addView(correct)
        questionBlock.addView(wrong1)
        questionBlock.addView(wrong2)
        questionBlock.addView(wrong3)

        container.addView(questionBlock)
        questionBlocks.add(listOf(question, correct, wrong1, wrong2, wrong3))
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun saveQuizToFirestore() {
        val title = findViewById<EditText>(R.id.editTextTitle).text.toString().trim()
        val description = findViewById<EditText>(R.id.editTextDescription).text.toString().trim()
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val questionsList = mutableListOf<Map<String, String>>()

        for ((index, inputs) in questionBlocks.withIndex()) {
            val questionText = inputs[0].text.toString().trim()
            val correct = inputs[1].text.toString().trim()
            val wrong1 = inputs[2].text.toString().trim()
            val wrong2 = inputs[3].text.toString().trim()
            val wrong3 = inputs[4].text.toString().trim()

            if (questionText.isEmpty() || correct.isEmpty() || wrong1.isEmpty() || wrong2.isEmpty() || wrong3.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields in question ${index + 1}", Toast.LENGTH_SHORT).show()
                return
            }

            val questionMap = mapOf(
                "question" to questionText,
                "correct" to correct,
                "wrong1" to wrong1,
                "wrong2" to wrong2,
                "wrong3" to wrong3
            )
            questionsList.add(questionMap)
        }

        val quizData = mapOf(
            "title" to title,
            "description" to description,
            "questions" to questionsList
        )

        firestore.collection("quizzes")
            .add(quizData)
            .addOnSuccessListener {
                Toast.makeText(this, "Quiz saved successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save quiz: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}