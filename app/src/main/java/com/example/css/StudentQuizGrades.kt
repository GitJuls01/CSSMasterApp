package com.example.css

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class StudentQuizGrades : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_student_quiz_grades)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val quizId = intent.getStringExtra("quizId") ?: ""
        fetchAllParticipants(quizId)

        val backButton = findViewById<ImageButton>(R.id.back_button)
        val settingButton = findViewById<ImageButton>(R.id.setting_button)

        backButton.setOnClickListener {
            finish()
        }
        settingButton.setOnClickListener {
            val intent = Intent(this, TeacherAccountSettings::class.java)
            startActivity(intent)
        }
    }


    private fun fetchAllParticipants(quizId: String) {
        val participantsContainer = findViewById<LinearLayout>(R.id.grades_container)
        participantsContainer.removeAllViews()

        val db = FirebaseFirestore.getInstance()

        db.collection("quizzes").document(quizId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {

                    val participants = (document.get("participants") as? List<*>)
                        ?.mapNotNull { it as? Map<String, Any> }
                        ?: emptyList()

                    for (participant in participants) {

                        val gradesView = layoutInflater.inflate(R.layout.grades_item_view, participantsContainer, false)
                        val studentName = gradesView.findViewById<TextView>(R.id.student_name)
                        val studentScore = gradesView.findViewById<TextView>(R.id.student_score)


                        val name = participant["name"] as? String ?: "Unknown"
                        val score = participant["score"] as? Number ?: 0

                        studentName.text = "Name: $name"
                        studentScore.text = "Score: $score"


                        // Create a TextView dynamically for each participant
//                        val textView = TextView(this).apply {
//                            text = "$email : $score"
//                            textSize = 16f
//                            setPadding(16, 16, 16, 16)
//                        }

                       participantsContainer.addView(gradesView)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch participants: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}