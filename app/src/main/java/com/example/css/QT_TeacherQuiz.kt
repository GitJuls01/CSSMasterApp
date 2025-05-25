package com.example.css

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class QT_TeacherQuiz : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var quizListContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qt_teacher_quiz)

        firestore = FirebaseFirestore.getInstance()
        quizListContainer = findViewById(R.id.quiz_list_container)
        quizListContainer.visibility = View.GONE // initially invisible

        fetchAndDisplayQuizzes()

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            finish() // Go back to the previous screen
        }
    }

    private fun fetchAndDisplayQuizzes() {
        firestore.collection("quizzes")
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
                        val quizView = inflater.inflate(R.layout.quiz_item, quizListContainer, false)

                        val titleView = quizView.findViewById<TextView>(R.id.title_quiz)
                        val descView = quizView.findViewById<TextView>(R.id.desc_quiz)
                        val itemsView = quizView.findViewById<TextView>(R.id.items_quiz)
                        val takeButton = quizView.findViewById<ImageButton>(R.id.btn_take_quiz)

                        titleView.text = title
                        descView.text = description
                        itemsView.text = getString(R.string.items_quiz, questionCount.toString())

                        takeButton.setOnClickListener {
                            val intent = Intent(this, QT_TeacherQuiz_MainGame::class.java)
                            intent.putExtra("quiz_id", quizId)
                            startActivity(intent)
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