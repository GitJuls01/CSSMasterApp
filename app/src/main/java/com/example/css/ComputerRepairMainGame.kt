package com.example.css

import android.app.Dialog
import android.content.ClipData
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.Handler
import android.os.Looper

class ComputerRepairMainGame : AppCompatActivity() {

    data class ToolStep(
        val name: String,
        val question: String,
        val imageResId: Int,
        val correctTag: String,
        val placeXPercent: Float, // 0.0f to 1.0f
        val placeYPercent: Float,  // 0.0f to 1.0f
        val width: Int,
        val height: Int
    )

    private var backPressedOnce = false

    private val steps = listOf(
        ToolStep("Motherboard", "Step 1: It is known for an alternative name as MOBO", R.drawable.cp_motherboard_3, "motherboard", 0.5f, 0.5f, 500, 450),
        ToolStep("CPU", "Step 2: It is known as the Central Processing Unit", R.drawable.cp_cpu, "cpu", 0.4407f, 0.5338f, 95, 95),
        ToolStep("RAM", "Step 3: It is known as RAM", R.drawable.cp_ram, "ram", 0.3365f, 0.4182f, 200, 200),
        ToolStep("GPU", "Step 4: It is known as GPU", R.drawable.cp_gpu, "gpu", 0.5605f, 0.3512f, 200, 140),
        ToolStep("HDD", "Step 5: It is known as HDD", R.drawable.cp_hdd, "hdd", 0.6145f, 0.3096f, 220, 150)
    )

    private var currentStepIndex = 0



    private val meaningImages = mapOf(
        "motherboard" to R.drawable.cp_mobo_meaning,
        "cpu" to R.drawable.cp_cpu_meaning,
        "ram" to R.drawable.cp_ram_meaning,
        "gpu" to R.drawable.cp_gpu_meaning,
        "hdd" to R.drawable.cp_hdd_meaning
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_computer_repair_main_game)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.assembly_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val questionImage = findViewById<ImageView>(R.id.question_image)
        val infoPopupImage = findViewById<ImageView>(R.id.info_popup_image)

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
            showExitDialog()
        }

        val questionText = findViewById<TextView>(R.id.question_text)
        val dropArea = findViewById<FrameLayout>(R.id.drop_area)
        val toolRow = findViewById<LinearLayout>(R.id.tool_row)

        fun loadCurrentStep() {
            if (currentStepIndex < steps.size) {
                questionText.text = steps[currentStepIndex].question
            } else {
                questionText.text = "All steps complete!"
            }
        }

        loadCurrentStep()

        dropArea.setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val draggedView = event.localState as ImageView
                    val tag = draggedView.tag.toString()
                    val currentStep = steps[currentStepIndex]

//                    if (tag == currentStep.correctTag) {
//                        (draggedView.parent as? ViewGroup)?.removeView(draggedView)
//
//                        val dropWidth = dropArea.width
//                        val dropHeight = dropArea.height
//
//                        val params = FrameLayout.LayoutParams(currentStep.width, currentStep.height)
//
//                        if (currentStep.correctTag == "motherboard") {
//                            // Fixed centered placement for motherboard
//                            params.leftMargin = (currentStep.placeXPercent * dropWidth - currentStep.width / 2).toInt()
//                            params.topMargin = (currentStep.placeYPercent * dropHeight - currentStep.height / 2).toInt()
//                        } else {
//                            // Drop exactly where the finger was
//                            val dropXPercent = event.x / dropWidth
//                            val dropYPercent = event.y / dropHeight
//
//                            val percentText = "Dropped at: X=${"%.4f".format(dropXPercent)}, Y=${"%.4f".format(dropYPercent)}"
//                            Log.d("DEBUG_PERCENT", percentText)
//                            Toast.makeText(this, percentText, Toast.LENGTH_SHORT).show()
//
//                            params.leftMargin = (event.x - currentStep.width / 2).toInt()
//                            params.topMargin = (event.y - currentStep.height / 2).toInt()
//                        }
//
//                        draggedView.layoutParams = params
//                        dropArea.addView(draggedView)
//                        draggedView.visibility = View.VISIBLE
//                        draggedView.setOnTouchListener(null)
//
//                        Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
//                        currentStepIndex++
//                        loadCurrentStep()
//                    }

//                    if (tag == currentStep.correctTag) {
//                        (draggedView.parent as? ViewGroup)?.removeView(draggedView)
//
//                        // Use predefined placement from ToolStep
//                        val dropWidth = dropArea.width
//                        val dropHeight = dropArea.height
//
//                        val params = FrameLayout.LayoutParams(currentStep.width, currentStep.height)
//                        params.leftMargin = (currentStep.placeXPercent * dropWidth - currentStep.width / 2).toInt()
//                        params.topMargin = (currentStep.placeYPercent * dropHeight - currentStep.height / 2).toInt()
//
//                        draggedView.layoutParams = params
//                        dropArea.addView(draggedView)
//                        draggedView.visibility = View.VISIBLE
//
//                        draggedView.setOnTouchListener(null)
//
//                        Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
//                        currentStepIndex++
//                        loadCurrentStep()
//                    }

                    if (tag == currentStep.correctTag) {
                        (draggedView.parent as? ViewGroup)?.removeView(draggedView)

                        val dropWidth = dropArea.width
                        val dropHeight = dropArea.height

                        val params = FrameLayout.LayoutParams(currentStep.width, currentStep.height)
                        params.leftMargin = (currentStep.placeXPercent * dropWidth - currentStep.width / 2).toInt()
                        params.topMargin = (currentStep.placeYPercent * dropHeight - currentStep.height / 2).toInt()

                        draggedView.layoutParams = params
                        dropArea.addView(draggedView)
                        draggedView.visibility = View.VISIBLE
                        draggedView.setOnTouchListener(null)

                        // Swap text with correct image
                        questionText.visibility = View.GONE
                        questionImage.setImageResource(R.drawable.cp_correct)
                        questionImage.visibility = View.VISIBLE

                        // Tap to show meaning
                        draggedView.setOnClickListener {
                            val meaningRes = meaningImages[tag]
                            meaningRes?.let {
                                infoPopupImage.setImageResource(it)
                                infoPopupImage.visibility = View.VISIBLE
                                infoPopupImage.translationY = dropArea.height.toFloat()
                                infoPopupImage.animate().translationY(0f).setDuration(300).start()
                            }
                        }

                        // Tap the info image to dismiss and load next step
                        infoPopupImage.setOnClickListener {
                            infoPopupImage.visibility = View.GONE
                            questionImage.visibility = View.GONE
                            questionText.visibility = View.VISIBLE

                            if (currentStepIndex == steps.size - 1) {
                                // LAST STEP: Go back to ComputerRepair.kt with "showCompletion"
                                val intent = Intent(this, ComputerRepair::class.java)
                                intent.putExtra("showCompletion", true)
                                startActivity(intent)
                                finish() // Optional: remove from back stack
                            } else {
                                // NEXT STEP: continue
                                currentStepIndex++
                                loadCurrentStep()
                            }
                        }


                    } else {
                        questionText.visibility = View.GONE
                        questionImage.setImageResource(R.drawable.cp_wrong)
                        questionImage.visibility = View.VISIBLE

                        // Optional: Restore question text after 2 seconds
                        Handler(Looper.getMainLooper()).postDelayed({
                            questionImage.visibility = View.GONE
                            questionText.visibility = View.VISIBLE
                        }, 2000)

                    }


                    true
                }
                else -> true
            }
        }





        steps.forEach { step ->
            val toolImage = ImageView(this)
            toolImage.setImageResource(step.imageResId)
            toolImage.layoutParams = LinearLayout.LayoutParams(170, 170)
            toolImage.tag = step.correctTag

            toolImage.setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    val data = ClipData.newPlainText("", "")

                    val shadowBuilder = View.DragShadowBuilder(view)

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        view.startDragAndDrop(data, shadowBuilder, view, 0)
                    } else {
                        @Suppress("DEPRECATION")
                        view.startDrag(data, shadowBuilder, view, 0)
                    }

                    view.performClick()
                    true
                } else false
            }


            toolRow.addView(toolImage)
        }
    }

    private fun showExitDialog() {
        val dialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_exit_game, null)

        val btnYes = view.findViewById<ImageButton>(R.id.btn_yes)
        val btnNo = view.findViewById<ImageButton>(R.id.btn_no)

        btnYes.setOnClickListener {
            dialog.dismiss()
            finish() // Exit the activity
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)

        // Optional: make the dialog background transparent
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Optional: center dialog and apply layout settings
        dialog.window?.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        dialog.window?.setGravity(Gravity.CENTER)

        dialog.setCancelable(true) // back button can cancel it
        dialog.show()
    }
}
