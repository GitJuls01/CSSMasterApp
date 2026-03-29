package com.example.css

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdminStudentActivitiesDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_student_activities_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backBtn = findViewById<ImageButton>(R.id.back_button)
        val settingBtn = findViewById<ImageButton>(R.id.setting_button)

        backBtn.setOnClickListener {
            val intent = Intent(this, AdminStudentActivities::class.java)
            startActivity(intent)
        }

        settingBtn.setOnClickListener {
            val intent = Intent(this, TeacherAccountSettings::class.java)
            startActivity(intent)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_leaderboard)

        // SAMPLE DATA (for testing) delete mo na lang
        val sampleList = listOf(
            AdminStudentActivitiesDetailsItem("Quiz 1", "Completed"),
            AdminStudentActivitiesDetailsItem("Quiz 2", "Completed"),
            AdminStudentActivitiesDetailsItem("Quiz 3", "Completed"),
            AdminStudentActivitiesDetailsItem("Quiz 4", "Not Yet Completed")
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = AdminStudentActivitiesAdapter(sampleList)
    }
}