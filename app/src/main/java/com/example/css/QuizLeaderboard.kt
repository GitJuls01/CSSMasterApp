package com.example.css

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class QuizLeaderboard : AppCompatActivity() {

    private var backPressedOnce = false
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz_leaderboard)
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


        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, QuizTime::class.java)
            startActivity(intent)
            finish()
        }

        fetchAllUsersTotalScores()
    }

    private fun fetchAllUsersTotalScores() {
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val userName = sharedPreferences.getString("name", "Default Name")

        val db = FirebaseFirestore.getInstance()
        db.collection("quiz_history")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val scoreMap = mutableMapOf<String, Int>()

                for (doc in querySnapshot.documents) {
                    val name = doc.getString("user_name") ?: "Unknown"
                    val score = doc.getLong("score")?.toInt() ?: 0
                    scoreMap[name] = (scoreMap[name] ?: 0) + score
                }

                val sortedScores = scoreMap.entries.sortedByDescending { it.value }

                val leaderboardContainer = findViewById<LinearLayout>(R.id.leaderboard_container)
                leaderboardContainer.removeAllViews()

                sortedScores.forEachIndexed { index, entry ->
                    val row = layoutInflater.inflate(R.layout.item_leaderboard_row, leaderboardContainer, false)

                    val rankText = row.findViewById<TextView>(R.id.tv_rank)
                    val nameText = row.findViewById<TextView>(R.id.tv_player_name)
                    val scoreText = row.findViewById<TextView>(R.id.tv_score)

                    rankText.text = "#${index + 1}"
                    nameText.text = entry.key
                    scoreText.text = entry.value.toString()

                    // ✅ Highlight the current user
                    if (entry.key == userName) {
                        nameText.setTypeface(null, android.graphics.Typeface.BOLD_ITALIC)
                        scoreText.setTypeface(null, android.graphics.Typeface.BOLD_ITALIC)
                        rankText.setTypeface(null, android.graphics.Typeface.BOLD_ITALIC)
                    }

                    leaderboardContainer.addView(row)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch leaderboard", Toast.LENGTH_SHORT).show()
                Log.e("QuizLeaderboard", "Error fetching leaderboard", e)
            }
    }


//    private fun fetchAllUsersTotalScores() {
//        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
//        val userName = sharedPreferences.getString("name", "Default Name")
//
//        val db = FirebaseFirestore.getInstance()
//        db.collection("quiz_history")
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                val scoreMap = mutableMapOf<String, Int>()
//
//                for (doc in querySnapshot.documents) {
//                    val name = doc.getString("user_name") ?: "Unknown"
//                    val score = doc.getLong("score")?.toInt() ?: 0
//                    scoreMap[name] = (scoreMap[name] ?: 0) + score
//                }
//
//                val sortedScores = scoreMap.entries.sortedByDescending { it.value }
//
//                val leaderboardContainer = findViewById<LinearLayout>(R.id.leaderboard_container)
//                leaderboardContainer.removeAllViews()
//
//                sortedScores.take(10).forEachIndexed { index, entry ->
//                    val row = layoutInflater.inflate(R.layout.item_leaderboard_row, leaderboardContainer, false)
//
//                    val rankText = row.findViewById<TextView>(R.id.tv_rank)
//                    val nameText = row.findViewById<TextView>(R.id.tv_player_name)
//                    val scoreText = row.findViewById<TextView>(R.id.tv_score)
//
//                    rankText.text = "#${index + 1}"
//                    nameText.text = entry.key
//                    scoreText.text = entry.value.toString()
//
//                    leaderboardContainer.addView(row)
//                }
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, "Failed to fetch leaderboard", Toast.LENGTH_SHORT).show()
//                Log.e("QuizLeaderboard", "Error fetching leaderboard", e)
//            }
//    }

}