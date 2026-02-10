package com.example.css

import android.app.Dialog
import android.content.ClipData
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.view.DragEvent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class ComputerRepairMainGame : AppCompatActivity() {

    data class Step(
        val question: String,
        val correctTag: String,
        val centerImage: Int
    )

    private val steps = listOf(
        Step("Step 1: It is known for an alternative name as MOBO", "motherboard", R.drawable.cr_center_1),
        Step("Step 2: It is known as the Central Processing Unit", "cpu", R.drawable.cr_center_2),
        Step("Step 3: It is known as RAM", "ram", R.drawable.cr_center_3),
        Step("Step 4: It is known as GPU", "gpu", R.drawable.cr_center_4),
        Step("Step 5: It is known as HDD", "hdd", R.drawable.cr_center_5)
    )

    private val hideHandler = Handler(Looper.getMainLooper())
    private val hideRunnable = Runnable {
        correctWrongText.visibility = View.GONE
    }

    private var currentStepIndex = 0
    private var dragEnabled = true
    private var lastToastTime = 0L

    private lateinit var questionText: TextView
    private lateinit var centeredImage: ImageView
    private lateinit var correctWrongText: TextView
    private lateinit var nextButton: MaterialButton
    private lateinit var toolRow: LinearLayout
    private lateinit var dropArea: View
    private lateinit var dropHint: View
    private lateinit var infoButton: ImageButton

    private var currentStepCorrect: Boolean = false
    private val currentStep: Int
        get() = currentStepIndex + 1 // step numbers are 1-based


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_computer_repair_main_game)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.assembly_main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        
        questionText = findViewById(R.id.question_text)
        centeredImage = findViewById(R.id.centered_image)
        correctWrongText = findViewById(R.id.correct_wrong_text)
        nextButton = findViewById(R.id.next_button)
        toolRow = findViewById(R.id.tool_row)
        dropArea = findViewById(R.id.drop_area)
        dropHint = findViewById(R.id.drop_hint)


        correctWrongText.visibility = View.GONE
        nextButton.visibility = View.GONE

        loadStep()

        setupTools()
        setupDropArea()
        setupNextButton()
        setupBackButton()

        infoButton = findViewById(R.id.info_button)

        infoButton.setOnClickListener {
            // Only show info if the user already dropped the correct component
            if (currentStepCorrect) {
                showInfoDialog(currentStep)
            } else {
                Toast.makeText(this, "Drop the correct component first!", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun loadStep() {
        val step = steps[currentStepIndex]
        questionText.text = step.question

        correctWrongText.visibility = View.GONE
        nextButton.visibility = View.GONE
    }

    // ---------------- DRAG & DROP (SetupPage2 style) ----------------

    private fun setupTools() {
        val tools = listOf(
            Pair(R.drawable.cr_motherboard, "motherboard"),
            Pair(R.drawable.cr_cpu, "cpu"),
            Pair(R.drawable.cr_ram, "ram"),
            Pair(R.drawable.cr_gpu, "gpu"),
            Pair(R.drawable.cr_hdd, "hdd")
        )

        tools.forEach { (img, tag) ->
            val iv = ImageView(this)
            iv.setImageResource(img)
            iv.tag = tag
            iv.layoutParams = LinearLayout.LayoutParams(170, 170)

            iv.setOnTouchListener { v, event ->
                if (!dragEnabled) {
                    val now = System.currentTimeMillis()
                    if (now - lastToastTime > 800) { // 👈 0.8 sec gap
                        Toast.makeText(
                            this@ComputerRepairMainGame,
                            "Click NEXT to continue",
                            Toast.LENGTH_SHORT
                        ).show()
                        lastToastTime = now
                    }
                    return@setOnTouchListener true
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        val data = ClipData.newPlainText("", "")
                        val shadow = View.DragShadowBuilder(v)

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            v.startDragAndDrop(data, shadow, v, 0)
                        } else {
                            @Suppress("DEPRECATION")
                            v.startDrag(data, shadow, v, 0)
                        }
                        true
                    }
                    else -> false
                }
            }

            toolRow.addView(iv)
        }
    }

    private fun setupDropArea() {
        dropArea.setOnDragListener { _, event ->
            when (event.action) {

                DragEvent.ACTION_DRAG_STARTED -> true

                DragEvent.ACTION_DROP -> {
//                    Toast.makeText(this, "DROP DETECTED", Toast.LENGTH_SHORT).show()

                    val draggedView = event.localState as ImageView
                    handleDrop(draggedView)
                    true
                }

                else -> true
            }
        }
    }

    private fun updateToolState(enabled: Boolean) {
        for (i in 0 until toolRow.childCount) {
            val tool = toolRow.getChildAt(i)

            tool.alpha = if (enabled) 1.0f else 0.4f  // gray-out
            tool.isClickable = true                  // keep receiving touches
            tool.isFocusable = true
        }
    }

    private fun handleDrop(draggedView: ImageView) {
        val step = steps[currentStepIndex]
        val tag = draggedView.tag.toString()

        hideHandler.removeCallbacks(hideRunnable)

        if (tag == step.correctTag) {
            currentStepCorrect = true
            dragEnabled = false
            updateToolState(false)
            toolRow.removeView(draggedView)

            dropHint.visibility = View.GONE
            dropArea.background = null

            centeredImage.setImageResource(step.centerImage)
            centeredImage.visibility = View.VISIBLE

            // ---------------- Correct text with inline icon ----------------
            val drawable = ContextCompat.getDrawable(this, R.drawable.baseline_info_24)
            val sizeInDp = (16 * resources.displayMetrics.density).toInt()
            drawable?.setBounds(0, 0, sizeInDp, sizeInDp)

            val text = "Correct!\n(Click icon for more information)"
            val spannable = SpannableString(text)

            // Make "Correct!" bigger
            spannable.setSpan(
                android.text.style.RelativeSizeSpan(1.6f),
                0,
                "Correct!".length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Make the rest of the text smaller and gray
            val restStart = text.indexOf("\n") + 1
            spannable.setSpan(
                android.text.style.ForegroundColorSpan(ContextCompat.getColor(this, android.R.color.darker_gray)),
                restStart,
                text.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Replace "icon" with drawable
            val start = text.indexOf("icon")
            val end = start + "icon".length
            drawable?.let {
                val imageSpan = ImageSpan(it, ImageSpan.ALIGN_BOTTOM)
                spannable.setSpan(imageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            correctWrongText.text = spannable
            correctWrongText.visibility = View.VISIBLE

            nextButton.visibility = View.VISIBLE

            // Change NEXT to DONE if this is the last step
            if (currentStepIndex == steps.size - 1) {
                nextButton.text = "DONE"
            } else {
                nextButton.text = "NEXT"
            }

        } else {
            // ---------------- Wrong text with bigger first word ----------------
            val text = "Wrong!\n(Please select the right component)"
            val spannable = SpannableString(text)

            // Make "Wrong!" bigger
            spannable.setSpan(
                android.text.style.RelativeSizeSpan(1.6f),
                0,
                "Wrong!".length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable.setSpan(
                android.text.style.ForegroundColorSpan(ContextCompat.getColor(this, android.R.color.holo_red_dark)),
                0,
                "Wrong!".length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Make the rest of the text smaller and gray
            val restStart = text.indexOf("\n") + 1
            spannable.setSpan(
                android.text.style.ForegroundColorSpan(ContextCompat.getColor(this, android.R.color.darker_gray)),
                restStart,
                text.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            correctWrongText.text = spannable
            correctWrongText.visibility = View.VISIBLE

            hideHandler.postDelayed(hideRunnable, 2000)
        }
    }

    private fun setupNextButton() {
        nextButton.setOnClickListener {
            dragEnabled = true
            currentStepCorrect = false
            updateToolState(true)

            currentStepIndex++

            if (currentStepIndex < steps.size) {
                loadStep()
            } else {
                Toast.makeText(this, "All steps completed!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    // ---------------- BACK BUTTON ----------------

    private fun setupBackButton() {
        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            showExitDialog()
        }
    }

    private fun showExitDialog() {
        val dialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_exit_game, null)

        view.findViewById<ImageButton>(R.id.btn_yes).setOnClickListener {
            dialog.dismiss()
            finish()
        }

        view.findViewById<ImageButton>(R.id.btn_no).setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.show()
    }

    private fun showInfoDialog(step: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_info)
        dialog.setCancelable(true)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Set dialog width to 85% of screen width
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.CENTER)

        val titleTextView = dialog.findViewById<TextView>(R.id.info_title)
        val meaningTextView = dialog.findViewById<TextView>(R.id.info_meaning)
        val closeButton = dialog.findViewById<ImageButton>(R.id.info_close)

        // Set content based on step
        when (step) {
            1 -> {
                titleTextView.text = "MOTHERBOARD (MAIN BOARD)"
                meaningTextView.text =
                    "Alternatively called the mb, mainboard, mboard, mobo, mobd, backplane board, base board, main circuit board, planar board, system board, or a logic board on Apple computers. The motherboard is a printed circuit board and foundation of a computer that is the biggest board in a computer chassis."
            }
            2 -> {
                titleTextView.text = "CENTRAL PROCESSING UNIT (CPU)"
                meaningTextView.text =
                    "A central processing unit (CPU) is the most important processor in a computer system. It executes instructions of a computer program, including arithmetic, logic, controlling, and input/output operations. The CPU is essentially the active brain of the computer."
            }
            3 -> {
                titleTextView.text = "RANDOM ACCESS MEMORY (RAM)"
                meaningTextView.text =
                    "Random access memory (RAM) is the hardware in a computing device that provides temporary storage for the operating system (OS), software programs and any other data in current use so they're quickly available to the device's processor."
            }
            4 -> {
                titleTextView.text = "GRAPHICS PROCESSING UNIT (GPU)"
                meaningTextView.text =
                    "Graphics processing technology has evolved to deliver unique benefits in the world of computing. The latest graphics processing units (GPUs) unlock new possibilities in gaming, content creation, machine learning, and more."
            }
            5 -> {
                titleTextView.text = "HARD DRIVE DISK (HDD)"
                meaningTextView.text =
                    "A Hard Disk Drive (sometimes abbreviated as a hard drive, HD, or HDD) is a non-volatile data storage device. It is usually installed internally in a computer, attached directly to the disk controller of the computer's motherboard."
            }
        }

        // Close button
        closeButton.setOnClickListener { dialog.dismiss() }

        // Show dialog last
        dialog.show()
    }
}
