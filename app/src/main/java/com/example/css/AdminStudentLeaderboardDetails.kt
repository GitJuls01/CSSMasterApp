package com.example.css

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AdminStudentLeaderboardDetails : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: AdminStudentLeaderboardAdapter
    private val leaderboardList = mutableListOf<AdminStudentLeaderboardDetailsItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_student_leaderboard_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firestore = FirebaseFirestore.getInstance()

        val backBtn = findViewById<ImageButton>(R.id.back_button)
        val settingBtn = findViewById<ImageButton>(R.id.setting_button)

        val quizId = intent.getStringExtra("quizId") ?: ""
        val quizTitle = intent.getStringExtra("quizTitle") ?: ""

        // Set the quiz title in the TextView
        val quizTitleText= findViewById<TextView>(R.id.quiz_title)
        quizTitleText.text = getString(R.string.quiz_label, quizTitle)

        backBtn.setOnClickListener {
            val intent = Intent(this, AdminStudentLeaderboard::class.java)
            startActivity(intent)
        }
        settingBtn.setOnClickListener {
            val intent = Intent(this, TeacherAccountSettings::class.java)
            startActivity(intent)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_leaderboard)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdminStudentLeaderboardAdapter(leaderboardList)
        recyclerView.adapter = adapter

        fetchParticipants(quizId)
    }
    private fun fetchParticipants(quizId: String) {
        firestore.collection("quizzes")
            .document(quizId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val participants = document.get("participants") as? List<Map<String, Any>>
                    val totalQuestions = (document.get("questions") as? List<*>)?.size ?: 1


                    leaderboardList.clear()

                    participants?.forEachIndexed { index, p ->
                        val name = p["name"]?.toString() ?: "Unknown"
                        val score = (p["score"] as? Long)?.toInt() ?: 0
                        val attempts = (p["attempts"] as? Long)?.toInt() ?: 0
                        val rank = index + 1 // simple ranking

                        // Calculate percentage
                        val percentage = if (totalQuestions > 0) {
                            ((score.toDouble() / totalQuestions) * 100).toInt()
                        } else 0

                        leaderboardList.add(
                            AdminStudentLeaderboardDetailsItem(name, percentage, attempts, rank)
                        )
                    }

                    // Sort by score highest → lowest
                    leaderboardList.sortByDescending { it.score }

                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }


}