package com.example.css

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.io.Serializable

class GuessCSSMainGame : AppCompatActivity() {

    data class Question(
        val imageResId: Int,
        val choices: List<String>,
        val correctAnswer: String,
        val description: String
    ) : Serializable

    private lateinit var currentQuestion: Question
    private lateinit var shuffledChoices: List<String>
    private var questionIndex = 0

    private lateinit var circularProgress: CircularProgressIndicator
    private lateinit var progressText: TextView
    private var questionTimer: CountDownTimer? = null

    private var backPressedOnce = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_guess_cssmain_game)

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
                    questionTimer?.cancel()
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

        circularProgress = findViewById(R.id.circularProgress)
        progressText = findViewById(R.id.progressText)

        // Back button logic
        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            questionTimer?.cancel()
            intent.putExtra("openFragment", "games")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        startCountdownTimer()

        // Sample questions
        val questions = listOf(
            Question(
                imageResId = R.drawable.gc_printer,
                choices = listOf("PRINTER", "MOUSE", "KEYBOARD", "SPEAKER"),
                correctAnswer = "PRINTER" ,
                description =  "A printer is a device that produces printed copies of documents or images, typically on paper, using ink or toner. It's a peripheral hardware component that receives data from a computer or other device and transforms it into a physical hard copy."
            ),
            Question(
                imageResId = R.drawable.gc_speaker,
                choices = listOf("MODEM", "CPU", "SPEAKER", "MOTHERBOARD"),
                correctAnswer = "SPEAKER",
                description =  "A speaker is a device that converts electrical signals into sound waves, allowing us to hear audio output from various devices like computers, smartphones, and televisions. They receive audio signals and, using a combination of a diaphragm, voice coil, and magnet, convert them into sound waves."
            ),
            Question(
                imageResId = R.drawable.gc_mouse,
                choices = listOf("MODEM", "DESKTOP", "RAM", "MOUSE"),
                correctAnswer = "MOUSE",
                description =  "A computer mouse is a small, hand-held input device used to control the cursor on a computer screen. It allows users to navigate the graphical user interface (GUI) by moving the mouse on a surface, which in turn moves the cursor on the screen. "
            ),
            Question(
                imageResId = R.drawable.gc_ram,
                choices = listOf("CPU", "RAM", "MOTHERBOARD", "HARD DISK"),
                correctAnswer = "RAM",
                description =  "Random Access Memory, is the temporary, volatile memory within a computer that stores the data and instructions the CPU needs to execute tasks quickly. It's like the computer's workspace, holding the active information for the current operations. Unlike storage devices like hard drives or SSDs, RAM loses its contents when the computer is powered off. "
            ),
            Question(
                imageResId = R.drawable.gc_cpu,
                choices = listOf("VIDEO CARD", "NETWORK CARD", "MEMORY CARD", "CPU"),
                correctAnswer = "CPU",
                description =  "(Central Processing Unit) is the primary component of a computer responsible for executing instructions and processing data. It acts as the \"brain\" of the computer, interpreting commands from software and controlling other components. The CPU is essentially a complex set of electronic circuitry that performs various operations, including arithmetic, logic, and data manipulation. "
            ),
            Question(
                imageResId = R.drawable.gc_keyboard,
                choices = listOf("MICROPHONE", "SPEAKER", "KEYBOARD", "CAMERA"),
                correctAnswer = "KEYBOARD",
                description =  "A keyboard is an input device used to enter characters and functions into a computer system. It's the primary tool for typing text, numbers, and symbols, and is often connected to a computer via a cable or wirelessly. "
            ),
            Question(
                imageResId = R.drawable.gc_gpu,
                choices = listOf("CPU", "GPU", "STORAGE DISK", "RAM"),
                correctAnswer = "GPU",
                description =  "A Graphics Processing Unit (GPU) is a specialized electronic circuit designed to accelerate the processing of digital images and computer graphics. It's essentially a chip that performs mathematical calculations at high speed, particularly those involved in rendering graphics and images. GPUs can be found in various devices, including discrete video cards, embedded on motherboards, mobile phones, and game consoles. "
            ),
            Question(
                imageResId = R.drawable.gc_flashd,
                choices = listOf("FLASH DRIVE", "USB", "STORAGE DISK", "DVD"),
                correctAnswer = "FLASH DRIVE",
                description =  "A flash drive is a small, portable data storage device that uses flash memory and a USB interface for connecting to computers and other devices. Also known as a thumb drive, pen drive, or jump drive, it's a versatile tool for transferring and storing files. Flash drives are commonly used for backing up data, carrying documents, photos, and videos, and transferring files between devices. "
            ),
            Question(
                imageResId = R.drawable.gc_laptop,
                choices = listOf("XEROX MACHINE", "COMPUTER", "SCANNER", "LAPTOP"),
                correctAnswer = "LAPTOP",
                description =  "A laptop, also known as a notebook, is a portable computer designed for use in various locations. It features a screen, keyboard, and touchpad integrated into a clamshell-style design, making it easy to carry and use on the go. Laptops offer the functionality of a desktop computer in a compact, battery-powered package. "
            ),
            Question(
                imageResId = R.drawable.gc_hdd,
                choices = listOf("FLASH DRIVE", "DVD", "FLOPPY DISK", "HARD DRIVE DISK"),
                correctAnswer = "HARD DRIVE DISK",
                description =  "A hard disk drive (HDD) is a data storage device that uses one or more rotating platters coated with a magnetic material to store digital data. It utilizes a read/write head to access and manipulate data on the platters. HDDs are a common type of data storage, used in both laptops and desktop computers. "
            )
        )

        questionIndex = intent.getIntExtra("questionIndex", 0)
        val loadNext = intent.getBooleanExtra("loadNext", false)

        if (loadNext) {
            questionIndex++
            if (questionIndex >= questions.size) {
                // All questions done, go to Congratulations screen
                // ✅ Cancel timer before navigating
                questionTimer?.cancel()
                val congratsIntent = Intent(this, GuessCSSCongratulation::class.java)
                startActivity(congratsIntent)
                finish()
                return
            }
        }

        // For now, just pick the first question
        currentQuestion = questions[questionIndex]
        shuffledChoices = currentQuestion.choices.shuffled()

        // Load image
        val questionImage = findViewById<ImageView>(R.id.tq_question_image)
        questionImage.setImageResource(currentQuestion.imageResId)

        // Load choices into TextViews
        findViewById<TextView>(R.id.choice_a_text).text = shuffledChoices[0]
        findViewById<TextView>(R.id.choice_b_text).text = shuffledChoices[1]
        findViewById<TextView>(R.id.choice_c_text).text = shuffledChoices[2]
        findViewById<TextView>(R.id.choice_d_text).text = shuffledChoices[3]

        // Set up click listeners for each choice
        val choiceA = findViewById<FrameLayout>(R.id.choice_a_container)
        val choiceB = findViewById<FrameLayout>(R.id.choice_b_container)
        val choiceC = findViewById<FrameLayout>(R.id.choice_c_container)
        val choiceD = findViewById<FrameLayout>(R.id.choice_d_container)

        choiceA.setOnClickListener { onChoiceSelected(shuffledChoices[0]) }
        choiceB.setOnClickListener { onChoiceSelected(shuffledChoices[1]) }
        choiceC.setOnClickListener { onChoiceSelected(shuffledChoices[2]) }
        choiceD.setOnClickListener { onChoiceSelected(shuffledChoices[3]) }
    }

    private fun onChoiceSelected(selectedAnswer: String) {
        // Cancel the timer when user selects an answer
        questionTimer?.cancel()
        val intent = Intent(this, GuessCSSAnswerPage::class.java)
        intent.putExtra("selected_answer", selectedAnswer)
        intent.putExtra("question_data", currentQuestion)
        intent.putExtra("questionIndex", questionIndex)
        startActivity(intent)
        finish()
    }

    private fun startCountdownTimer() {
        val totalTime = 20 // seconds
        circularProgress.max = totalTime

        questionTimer = object : CountDownTimer((totalTime * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000).toInt()
                circularProgress.progress = secondsLeft
                progressText.text = secondsLeft.toString()
            }

            override fun onFinish() {
                circularProgress.progress = 0
                progressText.text = "0"
                Toast.makeText(this@GuessCSSMainGame, "Time's up!", Toast.LENGTH_SHORT).show()

                // Automatically go to answer page with "You have no answer"
                val intent = Intent(this@GuessCSSMainGame, GuessCSSAnswerPage::class.java)
                intent.putExtra("selected_answer", "You have no answer")
                intent.putExtra("question_data", currentQuestion)
                intent.putExtra("questionIndex", questionIndex)
                startActivity(intent)
                finish()
            }
        }.start()
    }


}
