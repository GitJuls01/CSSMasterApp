package com.example.css

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.firestore.SetOptions

class CreateQuiz : AppCompatActivity() {

    private var questionCount = 1
    private lateinit var firestore: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences
    private var participantsList: List<*>? = null

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

        val backBtn = findViewById<ImageButton>(R.id.back_button)
        backBtn.setOnClickListener {
            finish() // Go back to the previous screen
        }


        val quizId = intent.getStringExtra("quizId")
        if (quizId != null) {
            loadQuizData(quizId)
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
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val userName = sharedPreferences.getString("name", "Default Name")

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
                Toast.makeText(
                    this,
                    "Please fill in all fields in question ${index + 1}",
                    Toast.LENGTH_SHORT
                ).show()
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

        val quizDataSaveUpdate = mutableMapOf(
            "created_by" to userName,
            "created_date" to com.google.firebase.Timestamp.now(),
            "title" to title,
            "description" to description,
            "questions" to questionsList,
            "participants" to participantsList
            //"isPosted" to false
        )

        // Check if editing or creating new
        val quizId = intent.getStringExtra("quizId")

        // Retrieve existing quiz data if editing
        quizId?.let { it ->
            firestore.collection("quizzes").document(it).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Preserve existing 'created_date' & participants if editing
                        val existingCreatedDate = document.getTimestamp("created_date")
                        val existingParticipants = document.get("participants") as? List<*>
                        existingCreatedDate?.let {
                            quizDataSaveUpdate["created_date"] = it
                        }
                        existingParticipants?.let {
                            quizDataSaveUpdate["participants"] = it
                        }
                    }
                }
                .addOnCompleteListener {
                    // Filter out null values from the map
                    val filteredData = quizDataSaveUpdate.filterValues { it != null }

                    // Proceed with saving the data
                    firestore.collection("quizzes")
                        .document(quizId)
                        .set(filteredData, SetOptions.merge())
                        .addOnSuccessListener {
                            Toast.makeText(this, "Quiz updated successfully!", Toast.LENGTH_SHORT)
                                .show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Failed to update quiz: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
        } ?: run {
            // Creating new quiz
            firestore.collection("quizzes")
                .add(quizDataSaveUpdate)
                .addOnSuccessListener {
                    Toast.makeText(this, "Quiz saved successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save quiz: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }
        }
    }

    private fun loadQuizData(quizId: String) {
        questionBlocks.clear()
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("quizzes").document(quizId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val title = document.getString("title") ?: ""
                    val description = document.getString("description") ?: ""
                    val questions = document.get("questions") as? List<Map<String, Any>> ?: emptyList()
                    participantsList = document.get("participants") as? List<*> ?: emptyList<Any>()

                    // Set title and description
                    findViewById<EditText>(R.id.editTextTitle).setText(title)
                    findViewById<EditText>(R.id.editTextDescription).setText(description)

                    // Load questions into UI
                    val container = findViewById<LinearLayout>(R.id.questionContainer)
                    container.removeAllViews()

                    questionCount = 0 // Reset counter
                    for (q in questions) {
                        questionCount++ // Increment for each existing question
                        val questionView = layoutInflater.inflate(R.layout.quiz_question_block, container, false)

                        val questionEdit = questionView.findViewById<EditText>(R.id.editTextQuestion1)
                        val correctEdit = questionView.findViewById<EditText>(R.id.editTextCorrectAnswer1)
                        val wrong1Edit = questionView.findViewById<EditText>(R.id.editTextWrongAnswer1_1)
                        val wrong2Edit = questionView.findViewById<EditText>(R.id.editTextWrongAnswer1_2)
                        val wrong3Edit = questionView.findViewById<EditText>(R.id.editTextWrongAnswer1_3)

                        questionEdit.setText(q["question"] as? String)
                        correctEdit.setText(q["correct"] as? String)
                        wrong1Edit.setText(q["wrong1"] as? String)
                        wrong2Edit.setText(q["wrong2"] as? String)
                        wrong3Edit.setText(q["wrong3"] as? String)

                        container.addView(questionView)
                        questionBlocks.add(listOf(questionEdit, correctEdit, wrong1Edit, wrong2Edit, wrong3Edit))
                    }
                }
            }
            .addOnFailureListener {
                Log.e("CreateQuiz", "Failed to load quiz", it)
            }
    }

}