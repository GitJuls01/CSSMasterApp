package com.example.css

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class QuizTime : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var backPressedOnce = false
    private var userRank: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz_time)

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
                    finishAffinity() // Properly exits the app from this point
                } else {
                    backPressedOnce = true
                    // Show a Toast message
                    Toast.makeText(
                        applicationContext,
                        "Double click to exit the app",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Reset backPressedOnce flag after 2 seconds
                    android.os.Handler().postDelayed({
                        backPressedOnce = false
                    }, 2000) // Reset after 2 seconds
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("openFragment", "games")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
        val quizLeaderboard = findViewById<ImageButton>(R.id.qt_leaderboard)
        quizLeaderboard.setOnClickListener {
            val intent = Intent(this, QuizLeaderboard::class.java)
            startActivity(intent)
        }

        val teacherQuizCategory = findViewById<ImageButton>(R.id.qt_teacher_quiz_category)
        teacherQuizCategory.setOnClickListener {
            val intent = Intent(this, QT_TeacherQuiz::class.java)
            startActivity(intent)
        }

        val hardwareQuizCategory = findViewById<ImageButton>(R.id.qt_hardware_category)
        hardwareQuizCategory.setOnClickListener {
            val intent = Intent(this, QT_ComputerHardware_MainGame::class.java)
            startActivity(intent)
        }

        val softwareQuizCategory = findViewById<ImageButton>(R.id.qt_software_category)
        softwareQuizCategory.setOnClickListener {
            val intent = Intent(this, QT_ComputerSoftware_MainGame::class.java)
            startActivity(intent)
        }

        val inventorsQuizCategory = findViewById<ImageButton>(R.id.qt_inventors_category)
        inventorsQuizCategory.setOnClickListener {
            val intent = Intent(this, QT_Inventors_MainGame::class.java)
            startActivity(intent)
        }

        // Access SharedPreferences
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val userName = sharedPreferences.getString("name", "Default Name")

        val greetingTextName = findViewById<TextView>(R.id.greeting_text)
        greetingTextName?.text = getString(R.string.name_label2, userName)

        fetchAllUsersTotalScores()
    }

    private fun fetchAllUsersTotalScores() {
        val db = FirebaseFirestore.getInstance()
        val userName = sharedPreferences.getString("name", null)

        if (userName == null) {
            Toast.makeText(this@QuizTime, "User not found", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("quiz_history")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val scoreMap = mutableMapOf<String, Int>()

                for (doc in querySnapshot.documents) {
                    val name = doc.getString("user_name") ?: continue
                    val score = doc.getLong("score")?.toInt() ?: 0
                    scoreMap[name] = (scoreMap[name] ?: 0) + score
                }

                val sortedScores = scoreMap.entries.sortedByDescending { it.value }
                userRank = sortedScores.indexOfFirst { it.key == userName } + 1

                val userRankText = findViewById<TextView>(R.id.rank_text)
                if (userRank != null && userRank!! > 0) {
                    userRankText.text = getString(R.string.user_rank, userRank)
                } else {
                    userRankText.text = getString(R.string.user_not_ranked)
                }

            }
            .addOnFailureListener { e ->
                Toast.makeText(this@QuizTime, "Failed to fetch leaderboard", Toast.LENGTH_SHORT)
                    .show()
                Log.e("QuizTime", "Error fetching leaderboard", e)
            }
    }
}
