package com.example.css

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AdminStudentLeaderboard : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_student_leaderboard)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val userName = sharedPreferences.getString("name", "") ?: ""
        firestore = FirebaseFirestore.getInstance()

        val backBtn = findViewById<ImageButton>(R.id.back_button)
        val settingBtn = findViewById<ImageButton>(R.id.setting_button)

        backBtn.setOnClickListener {
            val intent = Intent(this, AdminStudentDashboard::class.java)
            startActivity(intent)
        }

        settingBtn.setOnClickListener {
            val intent = Intent(this, TeacherAccountSettings::class.java)
            startActivity(intent)
        }
        fetchQuizzesCreatedBy(userName)
    }

    private fun fetchQuizzesCreatedBy(username: String) {
        val topStudentContainer = findViewById<LinearLayout>(R.id.top_student_container)
        topStudentContainer.removeAllViews() // Clear old data

        firestore.collection("quizzes")
            .whereEqualTo("created_by", username)
            .orderBy("created_date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val quizId = document.id
                    val title = document.getString("title") ?: "Untitled Quiz"
                    val participants = document.get("participants") as? List<Map<String, Any>> ?: emptyList()

                    val topScorer = participants.maxByOrNull {
                        (it["score"] as? Long ?: 0L).toInt()
                    }

                    val topName = topScorer?.get("name")?.toString() ?: "No participants"
//                    val topScore = (topScorer?.get("score") as? Long)?.toInt() ?: 0

                    val quizView = layoutInflater.inflate(R.layout.admin_student_leaderboard_item, topStudentContainer, false)

                    val titleView = quizView.findViewById<TextView>(R.id.quiz_text)
                    titleView.text = title

                    val topStudentView = quizView.findViewById<TextView>(R.id.top_student_name)
                    topStudentView.text = getString(R.string.rank_label, topName)

                    quizView.setOnClickListener {
                        //Toast.makeText(this, "Quiz deleted successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, AdminStudentLeaderboardDetails::class.java)
                        intent.putExtra("quizId", quizId)
                        intent.putExtra("quizTitle", title)
                        startActivity(intent)

                    }

                    topStudentContainer.addView(quizView)
                }
            }
            .addOnFailureListener { e ->
                Log.w("TeacherViewQuiz", "Error loading quizzes", e)
            }
    }

}