package com.example.css

import android.app.Dialog
import android.content.ClipData
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

class ComputerRepairMainGame : AppCompatActivity() {

    data class ToolStep(
        val name: String,
        val question: String,
        val imageResId: Int,
        val correctTag: String,
        val placeX: Float,
        val placeY: Float,
        val width: Int,
        val height: Int
    )

    private var backPressedOnce = false

    private val steps = listOf(
        ToolStep("Motherboard", "Step 1: It is known for an alternative name as MOBO", R.drawable.cp_motherboard_3, "motherboard", 0f, 0f, 500, 450),
        ToolStep("CPU", "Step 2: It is known as the Central Processing Unit", R.drawable.cp_cpu, "cpu", 325f, 180f, 95, 95),
        ToolStep("RAM", "Step 3: It is known as RAM", R.drawable.cp_ram, "ram", 181f, 78f, 200, 200),
        ToolStep("GPU", "Step 4: It is known as GPU", R.drawable.cp_gpu, "gpu", 362f, 88f, 200, 140),
        ToolStep("HDD", "Step 5: It is known as HDD", R.drawable.cp_hdd, "hdd", 399f, 62f, 220, 150)
    )

//    class OffsetDragShadowBuilder(view: View, private val touchX: Float, private val touchY: Float) :
//        View.DragShadowBuilder(view) {
//
//        override fun onProvideShadowMetrics(size: Point, touch: Point) {
//            val width = view.width
//            val height = view.height
//            size.set(width, height)
//            touch.set(touchX.toInt(), touchY.toInt()) // Set where your finger is inside the image
//        }
//    }


    private var currentStepIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_computer_repair_main_game)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.assembly_main)) { v, insets ->
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

                    if (tag == currentStep.correctTag) {
                        (draggedView.parent as? ViewGroup)?.removeView(draggedView)

                        val params = FrameLayout.LayoutParams(currentStep.width, currentStep.height)

                        if (currentStepIndex == 0) {
                            params.gravity = Gravity.CENTER
                        } else {
                            params.gravity = Gravity.TOP or Gravity.START
                        }

                        draggedView.layoutParams = params
                        dropArea.addView(draggedView)
                        draggedView.visibility = View.VISIBLE

                        if (currentStepIndex != 0) {
                            // Use actual drop position from event inside dropArea
                            draggedView.translationX = currentStep.placeX
                            draggedView.translationY = currentStep.placeY


                            // Debug log
//                            Log.d("DropPosition", "X: ${draggedView.translationX}, Y: ${draggedView.translationY}")
//                            Toast.makeText(this, "X: ${draggedView.translationX.toInt()}, Y: ${draggedView.translationY.toInt()}", Toast.LENGTH_SHORT).show()
                        }


                        draggedView.setOnTouchListener(null)

                        Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
                        currentStepIndex++
                        loadCurrentStep()
                    }
                    else {
                        Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show()
                        (draggedView.parent as? ViewGroup)?.removeView(draggedView)
                        draggedView.layoutParams = LinearLayout.LayoutParams(170, 170)
                        toolRow.addView(draggedView)
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

//                    val shadowBuilder = OffsetDragShadowBuilder(view, event.x, event.y)
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
