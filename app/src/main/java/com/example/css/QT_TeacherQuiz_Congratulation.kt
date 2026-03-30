package com.example.css

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class QT_TeacherQuiz_Congratulation : AppCompatActivity() {

    private var backPressedOnce = false
    private lateinit var firestore: FirebaseFirestore

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

        val quizId = intent.getStringExtra("quiz_id") ?: ""
        val lrn = intent.getStringExtra("lrn") ?: "" // pass current user email from previous activity
        val correctCount = intent.getIntExtra("correct_count", 0)
        val totalCount = intent.getIntExtra("total_count", 0)
        val percentage = intent.getIntExtra("percentage", 0)

        val summaryText = findViewById<TextView>(R.id.summary_text)
        val percentageText = findViewById<TextView>(R.id.score_text)
        summaryText.text = getString(R.string.quiz_summary, correctCount, totalCount)
        percentageText.text = getString(R.string.quiz_percentage, percentage)

        val backButton = findViewById<ImageButton>(R.id.back_button)
        val finishBtn = findViewById<ImageButton>(R.id.btn_finish)
        val retakeQuizBtn = findViewById<ImageButton>(R.id.btn_retake)

        val results = intent.getSerializableExtra("question_results") as? ArrayList<QT_TeacherQuiz_MainGame.QuestionResult>

//        Toast.makeText(this, ": ${quizId}", Toast.LENGTH_SHORT).show()
//        Toast.makeText(this, ": ${email}", Toast.LENGTH_SHORT).show()

        val db = FirebaseFirestore.getInstance()

        db.collection("quizzes").document(quizId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {

                    // Get participants as mutable list
                    val participants = (document.get("participants") as? List<*>)
                        ?.mapNotNull { it as? Map<String, Any> }
                        ?.toMutableList()
                        ?: mutableListOf()

                    // Flag to track if participant exists
                    var updated = false

                    // Loop to find participant and update score
                    for (i in participants.indices) {
                        val participant = participants[i]
                        if (participant["LRN"] == lrn) {
                            val updatedParticipant = participant.toMutableMap()
                            updatedParticipant["score"] = correctCount  // Replace with actual score variable
                            participants[i] = updatedParticipant
                            updated = true
                            break
                        }
                    }

                    // If participant not in list, add them
                    if (!updated) {
                        participants.add(mapOf("LRN" to lrn, "score" to correctCount))
                    }

                    // Write back updated participants list
                    db.collection("quizzes").document(quizId)
                        .update("participants", participants)
                        .addOnSuccessListener {
                            //Toast.makeText(this, "Score updated successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to update score: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }

        backButton.setOnClickListener {
            val intent = Intent(this, QT_TeacherQuiz::class.java)
            startActivity(intent)
            finish()
        }
        finishBtn.setOnClickListener {
            val intent = Intent(this, QTTeacherQuizThankYou::class.java)
            startActivity(intent)
            finish()
        }
        retakeQuizBtn.setOnClickListener {
//            val intent = Intent(this, ReviewAnswers::class.java)
//            intent.putExtra("question_results", results)
//            startActivity(intent)
//            finish()
        }

//        reviewAnswerBtn.setOnClickListener {
//            val intent = Intent(this, ReviewAnswers::class.java)
//            intent.putExtra("question_results", results)
//            startActivity(intent)
//            finish()
//        }
    }
}