package com.example.css

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class QuizTime : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz_time)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("openFragment", "games")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        val teacherQuizCategory = findViewById<ImageButton>(R.id.qt_teacher_quiz_category)
        teacherQuizCategory.setOnClickListener {
            val intent = Intent(this, QT_TeacherQuiz::class.java)
            startActivity(intent)
        }

        // Access SharedPreferences
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)

        val userName = sharedPreferences.getString("name", "Default Name") // Default name if not found

        val greetingTextName = findViewById<TextView>(R.id.greeting_text)

        greetingTextName?.text = getString(R.string.name_label2, userName)
    }
}
