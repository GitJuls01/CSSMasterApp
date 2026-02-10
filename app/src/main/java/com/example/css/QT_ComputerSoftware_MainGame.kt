package com.example.css

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.firestore.FirebaseFirestore

class QT_ComputerSoftware_MainGame : AppCompatActivity() {

    private lateinit var correctAnswerPopup: ImageView
    private lateinit var wrongAnswerPopup: ImageView

    private lateinit var quizTitle: TextView
    private lateinit var questionText: TextView

    private lateinit var choiceA: TextView
    private lateinit var choiceB: TextView
    private lateinit var choiceC: TextView
    private lateinit var choiceD: TextView

    private lateinit var containerA: FrameLayout
    private lateinit var containerB: FrameLayout
    private lateinit var containerC: FrameLayout
    private lateinit var containerD: FrameLayout

    private lateinit var containerList: List<FrameLayout>
    private lateinit var textViewList: List<TextView>

    private var questionList: List<SoftwareQuestion> = emptyList()
    private var currentQuestionIndex = 0
    private var correctAnswers = 0

    private lateinit var circularProgress: CircularProgressIndicator
    private lateinit var progressText: TextView
    private var questionTimer: CountDownTimer? = null

    private var backPressedOnce = false

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_qt_computer_hardware_main_game)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        MusicManager.play(this, R.raw.quizbgm)

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // Register the back press callback to handle back button presses
        val backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedOnce) {
                    // Exit the app
                    questionTimer?.cancel()
                    questionList = emptyList()
                    correctAnswers = 0
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


        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            MusicManager.stop()
            finish()
        }

        correctAnswerPopup = findViewById(R.id.CorrectAnswerPopup)
        wrongAnswerPopup = findViewById(R.id.WrongAnswerPopup)

        quizTitle = findViewById(R.id.tq_quiz_title)
        questionText = findViewById(R.id.tq_question)

        // FrameLayouts
        containerA = findViewById(R.id.choice_a_container)
        containerB = findViewById(R.id.choice_b_container)
        containerC = findViewById(R.id.choice_c_container)
        containerD = findViewById(R.id.choice_d_container)
        containerList = listOf(containerA, containerB, containerC, containerD)

        // TextViews inside FrameLayouts
        choiceA = findViewById(R.id.choice_a_text)
        choiceB = findViewById(R.id.choice_b_text)
        choiceC = findViewById(R.id.choice_c_text)
        choiceD = findViewById(R.id.choice_d_text)
        textViewList = listOf(choiceA, choiceB, choiceC, choiceD)

        circularProgress = findViewById(R.id.circularProgress)
        progressText = findViewById(R.id.progressText)


        // Load questions from Firestore
        FirebaseFirestore.getInstance().collection("quiz_software")
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val doc = snapshot.documents[0]
                    val rawQuestions = doc.get("questions") as? List<Map<String, Any>> ?: emptyList()

                    questionList = rawQuestions.mapNotNull { map ->
                        val question = map["question"] as? String
                        val correct = map["correct"] as? String
                        val wrong = map["wrong"] as? List<String>
                        if (question != null && correct != null && wrong != null && wrong.size == 3) {
                            SoftwareQuestion(question, correct, wrong)
                        } else null
                    }

                    if (questionList.isNotEmpty()) {
                        showQuestion(currentQuestionIndex)
                    } else {
                        Log.e("QuizData", "No valid questions found")
                    }
                } else {
                    Log.e("QuizData", "quiz_software collection is empty")
                }
            }
            .addOnFailureListener { e ->
                Log.e("QuizData", "Error fetching quiz_hardware: ", e)
            }
    }

    private fun showQuestion(index: Int) {
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val userName = sharedPreferences.getString("name", "Default Name")
        val quizId = "software_default_id"

        if (index >= questionList.size) {
            val quizHistory = hashMapOf(
                "id" to quizId,
                "user_name" to userName,
                "score" to correctAnswers,
                "created_date" to com.google.firebase.Timestamp.now()
            )
            val db = FirebaseFirestore.getInstance()

            // Query if a document exists with this user_email and quiz id
            db.collection("quiz_history")
                .whereEqualTo("user_name", userName)
                .whereEqualTo("id", quizId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        // Document exists, update the first matching document
                        val docId = querySnapshot.documents[0].id
                        db.collection("quiz_history").document(docId)
                            .update(
                                mapOf(
                                    "score" to correctAnswers,
                                    "created_date" to com.google.firebase.Timestamp.now()
                                )
                            )
                            .addOnSuccessListener {
                                goToCongratulationActivity(userName)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to update quiz result", Toast.LENGTH_SHORT).show()
                                Log.e("QTQuiz", "Error updating quiz history", e)
                            }
                    } else {
                        // No document, add new
                        db.collection("quiz_history")
                            .add(quizHistory)
                            .addOnSuccessListener {
                                goToCongratulationActivity(userName)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to save quiz result", Toast.LENGTH_SHORT).show()
                                Log.e("QTQuiz", "Error writing quiz history", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to query quiz history", Toast.LENGTH_SHORT).show()
                    Log.e("QTQuiz", "Error querying quiz history", e)
                }
            return
        }
        val question = questionList[index]
        questionText.text = question.question

        val choices = (question.wrong + question.correct).shuffled()

        textViewList.forEachIndexed { i, textView ->
            textView.text = choices[i]
        }

        containerList.forEachIndexed { i, container ->
            container.setOnClickListener {
                val selectedAnswer = textViewList[i].text.toString()
                checkAnswer(selectedAnswer, question.correct)
            }
        }

        updateQuizTitle(index + 1, questionList.size)
        startQuestionTimer()
    }

    // Helper function to start the congrats activity
    private fun goToCongratulationActivity(userName: String?) {
        val intent = Intent(this, QT_ComputerHardware_Congratulation::class.java).apply {
            putExtra("correct_count", correctAnswers)
            putExtra("total_count", questionList.size)
            putExtra("name", userName)
        }
        startActivity(intent)
        finish()
    }

    private fun updateQuizTitle(current: Int, total: Int) {
        quizTitle.text = "Question $current/$total"
    }

    private fun checkAnswer(selected: String, correct: String) {
        questionTimer?.cancel()
        if (selected == correct) {
            correctAnswers++
            correctAnswerPopup.visibility = View.VISIBLE
        } else {
            wrongAnswerPopup.visibility = View.VISIBLE
        }

        Handler(Looper.getMainLooper()).postDelayed({
            correctAnswerPopup.visibility = View.GONE
            wrongAnswerPopup.visibility = View.GONE
            currentQuestionIndex++
            showQuestion(currentQuestionIndex)
        }, 500) // 500ms = 0.5 seconds
    }

    data class SoftwareQuestion(
        val question: String = "",
        val correct: String = "",
        val wrong: List<String> = emptyList()
    )

    private fun startQuestionTimer() {
        val totalSeconds = 20
        circularProgress.max = totalSeconds
        circularProgress.progress = totalSeconds
        progressText.text = totalSeconds.toString()

        questionTimer?.cancel()
        questionTimer = object : CountDownTimer((totalSeconds * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000).toInt()
                circularProgress.progress = secondsLeft
                progressText.text = secondsLeft.toString()
            }

            override fun onFinish() {
                circularProgress.progress = 0
                progressText.text = "0"
                Toast.makeText(this@QT_ComputerSoftware_MainGame, "Time's up!", Toast.LENGTH_SHORT).show()
                wrongAnswerPopup.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    wrongAnswerPopup.visibility = View.GONE
                    currentQuestionIndex++
                    showQuestion(currentQuestionIndex)
                }, 500)
            }
        }.start()
    }

}
