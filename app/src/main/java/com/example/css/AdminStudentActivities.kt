package com.example.css

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AdminStudentActivities : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_student_activities)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val userGrade = sharedPreferences.getString("grade", "") ?: ""
        firestore = FirebaseFirestore.getInstance()

        val backBtn = findViewById<ImageButton>(R.id.back_button)
        val settingBtn = findViewById<ImageButton>(R.id.setting_button)

        // Set click listener

        backBtn.setOnClickListener {
            val intent = Intent(this, AdminStudentDashboard::class.java)
            startActivity(intent)
        }

        settingBtn.setOnClickListener {
            val intent = Intent(this, TeacherAccountSettings::class.java)
            startActivity(intent)
        }

        fetchAllStudentByGrade(userGrade)
    }

    private fun fetchAllStudentByGrade(userGrade: String) {
        val studentNameContainer = findViewById<LinearLayout>(R.id.student_name_container)

        studentNameContainer.removeAllViews() // Clear old data

        // Fetch students from Firestore where grade matches
        firestore.collection("users")
            .whereEqualTo("grade", userGrade)
            .whereEqualTo("role", "student")
            .whereEqualTo("isApproved", "true")
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(this, "No students found for grade $userGrade", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                for (document in result) {
                    val studentName = document.getString("name") ?: "No Name"

                    val studentView = layoutInflater.inflate(R.layout.admin_student_activities_item, studentNameContainer, false)
                    val viewDetails = studentView.findViewById<ImageView>(R.id.view_details)

                    // Example: assuming your layout has a TextView with id tvStudentName
                    val tvName = studentView.findViewById<TextView>(R.id.name_text)
                    tvName.text = studentName

                    studentView.setOnClickListener {
                        val intent = Intent(this, AdminStudentActivitiesDetails::class.java)
                        intent.putExtra("grade", userGrade)
                        intent.putExtra("name", studentName)
                        startActivity(intent)
                    }
                    viewDetails.setOnClickListener {
                        val intent = Intent(this, AdminStudentActivitiesDetails::class.java)
                        intent.putExtra("grade", userGrade)
                        intent.putExtra("name", studentName)
                        startActivity(intent)
                    }

                    // Add the view to the container
                    studentNameContainer.addView(studentView)
                }
            }
            .addOnFailureListener { e ->
                Log.w("TeacherViewStudent", "Error fetching students", e)
                Toast.makeText(this, "Failed to load students.", Toast.LENGTH_SHORT).show()
            }
    }

}