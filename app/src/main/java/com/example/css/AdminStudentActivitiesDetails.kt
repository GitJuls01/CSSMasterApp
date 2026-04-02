package com.example.css

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AdminStudentActivitiesDetails : AppCompatActivity() {

    private lateinit var adapter: AdminStudentActivitiesAdapter
    private val activityList = mutableListOf<AdminStudentActivitiesDetailsItem>()
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_student_activities_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firestore = FirebaseFirestore.getInstance()

        val backBtn = findViewById<ImageButton>(R.id.back_button)
        val settingBtn = findViewById<ImageButton>(R.id.setting_button)
        val tvName = findViewById<TextView>(R.id.name_text)
        val name = intent.getStringExtra("name")
        tvName.text = name

        backBtn.setOnClickListener {
            val intent = Intent(this, AdminStudentActivities::class.java)
            startActivity(intent)
        }

        settingBtn.setOnClickListener {
            val intent = Intent(this, TeacherAccountSettings::class.java)
            startActivity(intent)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_activities)
        recyclerView.layoutManager = LinearLayoutManager(this)  // ✅ Important
        adapter = AdminStudentActivitiesAdapter(activityList)
        recyclerView.adapter = adapter

        fetchAllQuizByGrade()
    }

    private fun fetchAllQuizByGrade() {
        val userGrade = intent.getStringExtra("grade") ?: ""
        val studentName = intent.getStringExtra("name") ?: ""

        firestore.collection("quizzes")
            .whereEqualTo("grade", userGrade)
            .orderBy("created_date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(this, "No quizzes found for grade $userGrade", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // Clear old data
                activityList.clear()

                for (document in result) {
                    val quizNumber = document.getString("title") ?: "No Title"
                    val participants = document.get("participants") as? List<Map<String, Any>> ?: emptyList()
                    // Check if student has completed the quiz
                    val status = if (participants.any { it["name"] == studentName }) {
                        "Completed"
                    } else {
                        "Not Yet Completed"
                    }

                    activityList.add(
                        AdminStudentActivitiesDetailsItem(quizNumber, status))
                }

                // Notify adapter that data has changed
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.w("AdminStudentActivities", "Error fetching quizzes", e)
                Toast.makeText(this, "Failed to load quizzes.", Toast.LENGTH_SHORT).show()
            }
    }

}