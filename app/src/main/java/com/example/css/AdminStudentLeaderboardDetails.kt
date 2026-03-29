package com.example.css

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdminStudentLeaderboardDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_student_leaderboard_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backBtn = findViewById<ImageButton>(R.id.back_button)
        val settingBtn = findViewById<ImageButton>(R.id.setting_button)

        backBtn.setOnClickListener {
            val intent = Intent(this, AdminStudentLeaderboard::class.java)
            startActivity(intent)
        }
        settingBtn.setOnClickListener {
            val intent = Intent(this, TeacherAccountSettings::class.java)
            startActivity(intent)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_leaderboard)

        // SAMPLE DATA (for testing) delete mo na lang
        val sampleList = listOf(
            AdminStudentLeaderboardDetailsItem("Marck Dulay", 100, 1),
            AdminStudentLeaderboardDetailsItem("Julius Abella", 90, 2),
            AdminStudentLeaderboardDetailsItem("Grace Leonor", 70, 3),
            AdminStudentLeaderboardDetailsItem("Juan Cruz", 85, 1),
            AdminStudentLeaderboardDetailsItem("Maria Santos", 60, 4),
            AdminStudentLeaderboardDetailsItem("Pedro Reyes", 95, 2)
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = AdminStudentLeaderboardAdapter(sampleList)
    }
}