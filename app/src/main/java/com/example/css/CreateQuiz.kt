package com.example.css

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast

class CreateQuiz : AppCompatActivity() {

    private var questionCount = 1
    private lateinit var firestore: FirebaseFirestore

    // Track all question blocks
    private val questionBlocks = mutableListOf<List<EditText>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_quiz)

        firestore = FirebaseFirestore.getInstance()

        // Register the first question block (already in XML)
        val question1 = findViewById<EditText>(R.id.editTextQuestion1)
        val correct1 = findViewById<EditText>(R.id.editTextCorrectAnswer1)
        val wrong1 = findViewById<EditText>(R.id.editTextWrongAnswer1_1)
        val wrong2 = findViewById<EditText>(R.id.editTextWrongAnswer1_2)
        val wrong3 = findViewById<EditText>(R.id.editTextWrongAnswer1_3)

        questionBlocks.add(listOf(question1, correct1, wrong1, wrong2, wrong3))

        val addQuestionBtn = findViewById<Button>(R.id.btnAddQuestion)
        addQuestionBtn.setOnClickListener {
            addQuestionBlock()
        }

        val saveBtn = findViewById<Button>(R.id.btnSubmitQuiz)
        saveBtn.setOnClickListener {
            saveQuizToFirestore()
        }
    }

    private fun addQuestionBlock() {
        questionCount++

        val questionBlock = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 24, 0, 24)
        }

        val question = EditText(this).apply {
            hint = "Question $questionCount"
        }

        val correct = EditText(this).apply {
            hint = "Correct Answer"
        }

        val wrong1 = EditText(this).apply {
            hint = "Wrong Answer 1"
        }

        val wrong2 = EditText(this).apply {
            hint = "Wrong Answer 2"
        }

        val wrong3 = EditText(this).apply {
            hint = "Wrong Answer 3"
        }

        val inputList = listOf(question, correct, wrong1, wrong2, wrong3)
        questionBlocks.add(inputList)

        questionBlock.addView(question)
        questionBlock.addView(correct)
        questionBlock.addView(wrong1)
        questionBlock.addView(wrong2)
        questionBlock.addView(wrong3)

        val container = findViewById<LinearLayout>(R.id.questionContainer)
        container.addView(questionBlock)
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