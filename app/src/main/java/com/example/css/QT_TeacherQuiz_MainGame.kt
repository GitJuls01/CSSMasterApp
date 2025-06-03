package com.example.css

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

class QT_TeacherQuiz_MainGame : AppCompatActivity() {

    private var backPressedOnce = false

    private lateinit var questionList: List<QuestionData>
    private var currentIndex = 0  // Keeps track of the current question index
    private lateinit var timerProgressBar: ProgressBar
    private var countDownTimer: CountDownTimer? = null
    private val totalTime = 15_000L
    private val interval = 100L
    private var correctAnswers = 0
    private lateinit var sharedPreferences: SharedPreferences

    private val questionResults = mutableListOf<QuestionResult>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_qt_teacher_quiz_main_game)

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
                    countDownTimer?.cancel()
                    currentIndex = 0
                    correctAnswers = 0
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
            finish() // Go back to the previous screen
        }

        timerProgressBar = findViewById(R.id.timer_progress)
        timerProgressBar.max = 100

        val quizId = intent.getStringExtra("quiz_id") ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("quizzes").document(quizId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val rawQuestions = document.get("questions") as? List<Map<String, Any>> ?: emptyList()

                    questionList = rawQuestions.mapNotNull { map ->
                        val question = map["question"] as? String
                        val correct = map["correct"] as? String
                        val wrong1 = map["wrong1"] as? String
                        val wrong2 = map["wrong2"] as? String
                        val wrong3 = map["wrong3"] as? String
                        if (question != null && correct != null && wrong1 != null && wrong2 != null && wrong3 != null) {
                            QuestionData(question, correct, listOf(wrong1, wrong2, wrong3))
                        } else null
                    }

                    if (questionList.isNotEmpty()) {
                        currentIndex = 0
                        showQuestion(questionList[currentIndex], currentIndex + 1, questionList.size)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("QTQuiz", "Error fetching quiz", e)
            }
    }

    data class QuestionData(
        val question: String,
        val correctAnswer: String,
        val wrongAnswers: List<String>
    )

    @Parcelize
    data class QuestionResult(
        val question: String,
        val correctAnswer: String,
        val selectedAnswer: String
    ) : Parcelable

    private fun showQuestion(data: QuestionData, index: Int, total: Int) {
        startTimer(data)
        val quizTitle = findViewById<TextView>(R.id.tq_quiz_title)
        val questionView = findViewById<TextView>(R.id.tq_question)
        val choiceAText = findViewById<TextView>(R.id.choice_a_text)
        val choiceBText = findViewById<TextView>(R.id.choice_b_text)
        val choiceCText = findViewById<TextView>(R.id.choice_c_text)
        val choiceDText = findViewById<TextView>(R.id.choice_d_text)

        val checkboxA = findViewById<CheckBox>(R.id.checkbox_a)
        val checkboxB = findViewById<CheckBox>(R.id.checkbox_b)
        val checkboxC = findViewById<CheckBox>(R.id.checkbox_c)
        val checkboxD = findViewById<CheckBox>(R.id.checkbox_d)

        val checkboxes = listOf(checkboxA, checkboxB, checkboxC, checkboxD)
        val labels = listOf(choiceAText, choiceBText, choiceCText, choiceDText)

        val allChoices = mutableListOf<String>().apply {
            add(data.correctAnswer)
            addAll(data.wrongAnswers)
        }.shuffled()

        quizTitle.text = "Question $index/$total"
        questionView.text = data.question
        choiceAText.text = "A) ${allChoices[0]}"
        choiceBText.text = "B) ${allChoices[1]}"
        choiceCText.text = "C) ${allChoices[2]}"
        choiceDText.text = "D) ${allChoices[3]}"

        // Reset checkboxes
        checkboxes.forEach { it.isChecked = false }

        // Allow only one checkbox to be checked
        checkboxes.forEach { selected ->
            selected.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    checkboxes.filter { it != selected }.forEach { it.isChecked = false }
                }
            }
        }

        val nextBtn = findViewById<ImageView>(R.id.btn_next)
        nextBtn.setOnClickListener {
            val selectedIndex = checkboxes.indexOfFirst { it.isChecked }
            countDownTimer?.cancel()

            if (selectedIndex == -1) {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show()
                startTimer(data)
                return@setOnClickListener
            }

            val selectedText = labels[selectedIndex].text.toString()
            val selectedAnswer = selectedText.substringAfter(") ").trim()

            // Track result
            questionResults.add(
                QuestionResult(
                    question = data.question,
                    correctAnswer = data.correctAnswer,
                    selectedAnswer = selectedAnswer
                )
            )

            if (selectedAnswer == data.correctAnswer) {
                correctAnswers++
            }

            goToNextQuestion()
        }

    }

    private fun startTimer(data: QuestionData) {
        countDownTimer?.cancel() // cancel previous timer if any
        timerProgressBar.progress = 100 // full progress at start

        countDownTimer = object : CountDownTimer(totalTime, interval) {
            override fun onTick(millisUntilFinished: Long) {
                // Calculate progress as percentage
                val progress = ((millisUntilFinished.toFloat() / totalTime) * 100).toInt()
                timerProgressBar.progress = progress
            }

            override fun onFinish() {
                timerProgressBar.progress = 0
                Toast.makeText(this@QT_TeacherQuiz_MainGame, "Time's up!", Toast.LENGTH_SHORT).show()

                // Check if user already answered this question
                val isAlreadyAnswered = questionResults.any { it.question == data.question }

                if (!isAlreadyAnswered) {
                    // Track unanswered question
                    questionResults.add(
                        QuestionResult(
                            question = data.question,
                            correctAnswer = data.correctAnswer,
                            selectedAnswer = "No answer"
                        )
                    )
                }
                goToNextQuestion()
            }
        }.start()
    }
    private fun goToNextQuestion() {
        currentIndex++
        if (currentIndex < questionList.size) {
            showQuestion(questionList[currentIndex], currentIndex + 1, questionList.size)
        } else {

            sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
            val userName = sharedPreferences.getString("name", "Default Name")

            val percentage = ((correctAnswers.toFloat() / questionList.size) * 100).toInt()
            val quizHistory = hashMapOf(
                "id" to intent.getStringExtra("quiz_id"),
                "user_name" to userName,
                "score" to correctAnswers,
                "created_date" to com.google.firebase.Timestamp.now()
            )

            val db = FirebaseFirestore.getInstance()
            db.collection("quiz_history")
                .add(quizHistory)
                .addOnSuccessListener {
                    val intent = Intent(this, QT_TeacherQuiz_Congratulation::class.java)
                    intent.putExtra("correct_count", correctAnswers)
                    intent.putExtra("total_count", questionList.size)
                    intent.putExtra("percentage", percentage)
                    intent.putParcelableArrayListExtra(
                        "question_results",
                        ArrayList(questionResults) // Convert to ArrayList for Intent
                    )
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save quiz result", Toast.LENGTH_SHORT).show()
                    Log.e("QTQuiz", "Error writing quiz history", e)
                }

//            // Navigate to QT_TeacherQuiz_Congratulation
//            val intent = Intent(this, QT_TeacherQuiz_Congratulation::class.java)
//            intent.putExtra("correct_count", correctAnswers)
//            intent.putExtra("total_count", questionList.size)
//            val percentage = ((correctAnswers.toFloat() / questionList.size) * 100).toInt()
//            intent.putExtra("percentage", percentage)
//            startActivity(intent)
//            finish()


        }
    }


}
