package com.example.css

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
import com.google.firebase.firestore.Query

class QT_TeacherQuiz : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var quizListContainer: LinearLayout
    private var backPressedOnce = false
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_qt_teacher_quiz)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firestore = FirebaseFirestore.getInstance()
        quizListContainer = findViewById(R.id.quiz_list_container)
        quizListContainer.visibility = View.GONE // initially invisible

        // Access SharedPreferences
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)

        fetchAndDisplayQuizzes()

//        val backButton = findViewById<ImageButton>(R.id.back_button)
//        backButton.setOnClickListener {
//            val intent = Intent(this, QuizTime::class.java)
//            startActivity(intent)
//            finish()
//        }

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("openFragment", "quizzes")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
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

    }

    private fun fetchAndDisplayQuizzes() {
        val userEmail = sharedPreferences.getString("email", "Default Email")

        firestore.collection("quizzes")
            //.whereEqualTo("isPosted", true)
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
                        val participants = doc.get("participants") as? List<*> ?: emptyList<Any>()

                        val quizView = inflater.inflate(R.layout.quiz_item, quizListContainer, false)

                        val titleView = quizView.findViewById<TextView>(R.id.title_quiz)
                        val descView = quizView.findViewById<TextView>(R.id.desc_quiz)
                        val itemsView = quizView.findViewById<TextView>(R.id.items_quiz)
                        val takeButton = quizView.findViewById<ImageButton>(R.id.btn_take_quiz)

                        titleView.text = title
                        descView.text = description
                        itemsView.text = getString(R.string.items_quiz, questionCount.toString())

                        val alreadyTaken = participants.contains(userEmail)

                        // Dim the button immediately if already participated
                        if (alreadyTaken) {
                            takeButton.alpha = 0.5f
                        }

                        takeButton.setOnClickListener {
                            // Check if user has already participated
                            if (alreadyTaken) {
                                Toast.makeText(this, "You already took this quiz: $title", Toast.LENGTH_SHORT).show()
                            }  else {
                                firestore.collection("quizzes").document(quizId)
                                    .update("participants", com.google.firebase.firestore.FieldValue.arrayUnion(userEmail))
                                    .addOnSuccessListener {
                                        val intent =
                                            Intent(this, QT_TeacherQuiz_MainGame::class.java)
                                        intent.putExtra("quiz_id", quizId)
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener { e -> Toast.makeText(this, "Failed to join quiz: ${e.message}", Toast.LENGTH_SHORT).show()
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

}