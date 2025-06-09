package com.example.css

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TeacherViewQuiz : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_teacher_view_quiz)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firestore = FirebaseFirestore.getInstance()

        // Set OnClickListener to navigate to TeacherDashboard
        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, TeacherDashboard::class.java)
            startActivity(intent)
        }

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)

    }

    override fun onResume() {
        super.onResume()
        val userName = sharedPreferences.getString("name", "Default Name")
        if (userName != null) {
            fetchQuizzesCreatedBy(userName)
        }
    }

    private fun fetchQuizzesCreatedBy(username: String) {
        val quizContainer = findViewById<LinearLayout>(R.id.quiz_container)
        quizContainer.removeAllViews() // Clear old data

        firestore.collection("quizzes")
            .whereEqualTo("created_by", username)
            .orderBy("created_date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val title = document.getString("title") ?: "Untitled Quiz"
                    val participantsList = document.get("participants") as? List<*> ?: emptyList<Any>()
                    val participantsCount = participantsList.size

                    val quizView = layoutInflater.inflate(R.layout.quiz_item_view, quizContainer, false)

                    val titleView = quizView.findViewById<TextView>(R.id.quiz_title)
                    val infoView = quizView.findViewById<TextView>(R.id.quiz_info)

                    titleView.text = title
                    infoView.text = getString(R.string.quiz_info_text, participantsCount, 0)

                    // Optional: Set click listeners
                    val editButton = quizView.findViewById<TextView>(R.id.edit_text)
                    val deleteButton = quizView.findViewById<TextView>(R.id.delete_text)

                    editButton.setOnClickListener {
                        val intent = Intent(this, CreateQuiz::class.java)
                        intent.putExtra("quizId", document.id)
                        startActivity(intent)
                    }

                    deleteButton.setOnClickListener {
                        val dialog = AlertDialog.Builder(this)
                            .setTitle("Delete Quiz")
                            .setMessage("Are you sure you want to delete this quiz?")
                            .setPositiveButton("Delete") { _, _ ->
                                firestore.collection("quizzes").document(document.id)
                                    .delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Quiz deleted successfully", Toast.LENGTH_SHORT).show()
                                        fetchQuizzesCreatedBy(username) // Refresh the list
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Failed to delete quiz: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .setNegativeButton("Cancel", null)
                            .create()

                        dialog.setOnShowListener {
                            val darkGreen = getColor(R.color.dark_green) // Use `getColor()` in activity
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(darkGreen)
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(darkGreen)
                        }

                        dialog.show()
                    }
                    quizContainer.addView(quizView)
                }
            }
            .addOnFailureListener { e ->
                Log.w("TeacherViewQuiz", "Error loading quizzes", e)
            }
    }


}