package com.example.css

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class QuizTime : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private var userRank: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate fragment layout (you can keep using activity_quiz_time.xml,
        // but it's better to rename it to fragment_quiz_time.xml)
        return inflater.inflate(R.layout.activity_quiz_time, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Back button → go back to previous fragment
//        val backButton = view.findViewById<ImageButton>(R.id.back_button)
//        backButton.setOnClickListener {
//            requireActivity().supportFragmentManager.popBackStack()
//        }

        // Leaderboard button
        val quizLeaderboard = view.findViewById<ImageButton>(R.id.qt_leaderboard)
        quizLeaderboard.setOnClickListener {
            val intent = Intent(requireContext(), QuizLeaderboard::class.java)
            startActivity(intent)
        }

        // Teacher quiz category
        val teacherQuizCategory = view.findViewById<ImageButton>(R.id.qt_teacher_quiz_category)
        teacherQuizCategory.setOnClickListener {
            val intent = Intent(requireContext(), QT_TeacherQuiz::class.java)
            startActivity(intent)
        }

        // Hardware quiz category
        val hardwareQuizCategory = view.findViewById<ImageButton>(R.id.qt_hardware_category)
        hardwareQuizCategory.setOnClickListener {
            showCountdownDialog {
                val intent = Intent(requireContext(), QT_ComputerHardware_MainGame::class.java)
                startActivity(intent)
            }
        }
        // Software quiz category
        val softwareQuizCategory = view.findViewById<ImageButton>(R.id.qt_software_category)
        softwareQuizCategory.setOnClickListener {
            showCountdownDialog {
                val intent = Intent(requireContext(), QT_ComputerSoftware_MainGame::class.java)
                startActivity(intent)
            }
        }
        // Inventors quiz category
        val inventorsQuizCategory = view.findViewById<ImageButton>(R.id.qt_inventors_category)
        inventorsQuizCategory.setOnClickListener {
            showCountdownDialog {
                val intent = Intent(requireContext(), QT_Inventors_MainGame::class.java)
                startActivity(intent)
            }
        }

        // Access SharedPreferences
        sharedPreferences =
            requireContext().getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
        val userName = sharedPreferences.getString("name", "Default Name")

        // Set greeting text
        val greetingTextName = view.findViewById<TextView>(R.id.greeting_text)
        greetingTextName?.text = getString(R.string.name_label2, userName)

        // Fetch user rank & leaderboard
        fetchAllUsersTotalScores(view)
    }

    private fun fetchAllUsersTotalScores(view: View) {
        val db = FirebaseFirestore.getInstance()
        val userName = sharedPreferences.getString("name", null)

        if (userName == null) {
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
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

                val userRankText = view.findViewById<TextView>(R.id.rank_text)
                if (userRank != null && userRank!! > 0) {
                    userRankText.text = getString(R.string.user_rank, userRank)
                } else {
                    userRankText.text = getString(R.string.user_not_ranked)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to fetch leaderboard", Toast.LENGTH_SHORT).show()
                Log.e("QuizTimeFragment", "Error fetching leaderboard", e)
            }
    }

    private fun showCountdownDialog(onFinish: () -> Unit) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.loading_quiz_countdown, null)
        val countdownText = dialogView.findViewById<TextView>(R.id.countdown_text)

        val dialog = AlertDialog.Builder(requireContext())
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

}
