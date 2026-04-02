package com.example.css

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class QT_TeacherQuiz : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var quizListContainer: LinearLayout
    private var backPressedOnce = false
    private lateinit var sharedPreferences: SharedPreferences
    private var attempts: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_qt_teacher_quiz)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        MusicManager.play(this, R.raw.quizbgm)

        firestore = FirebaseFirestore.getInstance()
        quizListContainer = findViewById(R.id.quiz_list_container)
        quizListContainer.visibility = View.GONE // initially invisible

        // Access SharedPreferences
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)

        fetchAndDisplayQuizzes()

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("openFragment", "quizzes")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            MusicManager.stop()
            finish()
        }

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // Register the back press callback to handle back button presses
        val backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedOnce) {
                    // Exit the app
                    MusicManager.stop()
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

    }

    private fun fetchAndDisplayQuizzes() {
        val userLrn = sharedPreferences.getString("LRN", "Default LRN")
        val userName = sharedPreferences.getString("name", "Default Name")
        val userGrade = sharedPreferences.getString("grade", "Default Grade")


        firestore.collection("quizzes")
            .whereEqualTo("grade", userGrade)
            .orderBy("created_date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    quizListContainer.removeAllViews()
                    val inflater = LayoutInflater.from(this)

                    for (doc in snapshot.documents) {
                        val quizId = doc.id
                        val title = doc.getString("title") ?: "Untitled Quiz"
                        val description = doc.getString("description") ?: ""
                        val questionCount = (doc.get("questions") as? List<*>)?.size ?: 0
                        val participants = (doc.get("participants") as? List<*>)
                            ?.mapNotNull { it as? Map<String, Any> }
                            ?.toMutableList()
                            ?: mutableListOf()


                        val quizView = inflater.inflate(R.layout.quiz_item, quizListContainer, false)

                        val titleView = quizView.findViewById<TextView>(R.id.title_quiz)
                        val descView = quizView.findViewById<TextView>(R.id.desc_quiz)
                        val itemsView = quizView.findViewById<TextView>(R.id.items_quiz)
                        val takeButton = quizView.findViewById<ImageButton>(R.id.btn_take_quiz)

                        titleView.text = title
                        descView.text = description
                        itemsView.text = getString(R.string.items_quiz, questionCount.toString())

                        val existing = participants.find { it["LRN"] == userLrn }
                        attempts = (existing?.get("attempts") as? Long)?.toInt() ?: 0

                        if (existing != null && attempts >= 5) {
                            takeButton.alpha = 0.5f
                        }

                        titleView.setOnClickListener {
                            showTitleAndDescription(title, description )
                        }

                        descView.setOnClickListener {
                            showTitleAndDescription(title, description )
                        }

                        takeButton.setOnClickListener {
                            // Check if user has reached maximum attempts
                            if (existing != null && attempts >= 5) {
                                Toast.makeText(this, "$attempts out of 5 attempts", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }

                            showCountdownDialog {

                                val quizRef = firestore.collection("quizzes").document(quizId)

                                // Fetch participants to check if LRN exists
                                quizRef.get().addOnSuccessListener { document ->

                                    val participants = document.get("participants") as? List<Map<String, Any>> ?: emptyList()

                                    // Only add if LRN not already in participants
                                    val existing = participants.any { it["LRN"] == userLrn }

                                    if (!existing) {
                                        val participantData = mapOf(
                                            "name" to userName,
                                            "LRN" to userLrn,
                                            "score" to 0,
                                            "attempts" to 0,
                                        )

                                        quizRef.update(
                                            "participants",
                                            com.google.firebase.firestore.FieldValue.arrayUnion(participantData)
                                        ).addOnFailureListener { e ->
                                            Toast.makeText(this, "Failed to join quiz: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    // ALWAYS go to the game
                                    val intent = Intent(this, QT_TeacherQuiz_MainGame::class.java)
                                    intent.putExtra("quiz_id", quizId)
                                    intent.putExtra("attempts", attempts + 1) // increment attempt
                                    startActivity(intent)
                                }
                            }
                        }
                        quizListContainer.addView(quizView)
                    }

                    quizListContainer.visibility = View.VISIBLE
                } else {
                    Toast.makeText(this, "No quizzes found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load quizzes: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showCountdownDialog(onFinish: () -> Unit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.loading_quiz_countdown, null)
        val countdownText = dialogView.findViewById<TextView>(R.id.countdown_text)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Get Ready!")
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.show()

        object : CountDownTimer(6000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                countdownText.text = "The quiz will start in $secondsLeft..."
            }

            override fun onFinish() {
                dialog.dismiss()
                onFinish()
            }
        }.start()
    }

    private fun showTitleAndDescription(title: String, description: String) {
        val dialog = AlertDialog.Builder(this) // use "requireContext()" if in Fragment
            .setTitle(title)
            .setMessage(description)
            .setPositiveButton("Close") { d, _ ->
                d.dismiss()
            }
            .create()

        dialog.show()
    }
}