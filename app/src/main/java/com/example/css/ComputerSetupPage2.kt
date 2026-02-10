package com.example.css

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import kotlin.random.Random
import android.graphics.Rect
import android.view.MotionEvent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ComputerSetupPage2 : AppCompatActivity() {

    private lateinit var partCase: ImageView
    private lateinit var remainingParts: List<ImageView>
    private lateinit var bootButton: MaterialButton
    private lateinit var nextButton: MaterialButton
    private lateinit var tryBootText: TextView
    private lateinit var finalText1: TextView
    private lateinit var finalText2: TextView

    // CORRECT COMPUTER PART FLOW
    private val correctOrder = listOf(
        R.id.part_motherboard,
        R.id.part_cpu,
        R.id.part_cpu_fan,
        R.id.part_ram,
        R.id.part_hdd
    )

    private val correctCaseImages = listOf(
        R.drawable.cs_comp_case_2_1,
        R.drawable.cs_comp_case_2_2,
        R.drawable.cs_comp_case_2_3,
        R.drawable.cs_comp_case_2_4,
        R.drawable.cs_comp_case_2_5,
        R.drawable.cs_comp_case_2_6
    )

    private val userOrder = mutableListOf<Int>()
    private var currentStep = 0
    private var wrongFlow = false

    // PERIPHERAL PARTS
    private val periCorrectOrder = listOf(
        R.id.peri_cpu,
        R.id.peri_monitor,
        R.id.peri_keyboard,
        R.id.peri_mouse
    )

    private val periImages = listOf(
        R.drawable.cs_peri_table_2_1,
        R.drawable.cs_peri_table_2_2,
        R.drawable.cs_peri_table_2_3,
        R.drawable.cs_peri_table_2_4,
        R.drawable.cs_peri_table_2_5
    )

    private val periUserOrder = mutableListOf<Int>()
    private var periStep = 0

    private var currentPhase = 1 // 1 = computer assembly, 2 = peripheral setup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_computer_setup_page2)

        // Handle system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        partCase = findViewById(R.id.part_case)
        bootButton = findViewById(R.id.boot_button)
        nextButton = findViewById(R.id.next_button)
        tryBootText = findViewById(R.id.try_boot_text)
        finalText1 = findViewById(R.id.final_text_1)
        finalText2 = findViewById(R.id.final_text_2)

        tryBootText.visibility = View.GONE
        nextButton.visibility = View.GONE
        bootButton.visibility = View.GONE
        finalText1.visibility = View.GONE
        finalText2.visibility = View.GONE

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener { finish() }

        remainingParts = listOf(
            findViewById(R.id.part_motherboard),
            findViewById(R.id.part_cpu),
            findViewById(R.id.part_cpu_fan),
            findViewById(R.id.part_ram),
            findViewById(R.id.part_hdd)
        )

        // Computer parts click listener
        remainingParts.forEach { part ->
            part.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Store touch offset and original position
                        v.tag = Triple(event.rawX - v.x, event.rawY - v.y, Pair(v.x, v.y))
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val (dx, dy, _) = v.tag as Triple<Float, Float, Pair<Float, Float>>
                        v.x = event.rawX - dx
                        v.y = event.rawY - dy
                    }
                    MotionEvent.ACTION_UP -> {
                        val (_, _, origPos) = v.tag as Triple<Float, Float, Pair<Float, Float>>
                        val caseRect = Rect()
                        partCase.getHitRect(caseRect)

                        val partRect = Rect(v.x.toInt(), v.y.toInt(), (v.x + v.width).toInt(), (v.y + v.height).toInt())

                        if (Rect.intersects(caseRect, partRect)) {
                            handlePartDrop(v as ImageView)
                        } else {
                            // Animate back to original position
                            v.animate().x(origPos.first).y(origPos.second).setDuration(300).start()
                        }
                    }
                }
                true
            }
        }


        partCase.setOnDragListener { _, event ->
            val draggedView = event.localState as? View
            when (event.action) {
                android.view.DragEvent.ACTION_DROP -> {
                    draggedView?.let { part ->
                        handlePartDrop(part as ImageView)
                    }
                    true
                }
                else -> true
            }
        }

        nextButton.setOnClickListener {
            showPeripheralParts()
        }

        bootButton.setOnClickListener {
            handleBootButton()
        }

        findViewById<ImageButton>(R.id.try_again_button).setOnClickListener { recreate() }
        findViewById<ImageButton>(R.id.home_button).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("openFragment", "games")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            MusicManager.stop()
            finish()
        }
    }

    // Separate function to handle drop logic
    private fun handlePartDrop(part: ImageView) {
        if (currentStep >= correctCaseImages.size - 1) return
        userOrder.add(part.id)

        if (!wrongFlow && part.id != correctOrder[currentStep]) {
            wrongFlow = true
        }

        if (wrongFlow) {
            // Keep random animation for wrong flow
            animatePartToCenterRandom(part)
        } else {
            // Correct flow: DON'T animate, just hide part and change case
            partCase.setImageResource(correctCaseImages[currentStep + 1])
            part.visibility = View.GONE
        }

        currentStep++

        if (currentStep == remainingParts.size) {
            if (wrongFlow) {
                partCase.postDelayed({
                    tryBootText.text = "LET'S SET UP THE PERIPHERALS"
                    tryBootText.visibility = View.VISIBLE
                    nextButton.visibility = View.VISIBLE
                }, 800)
            } else {
                tryBootText.text = "SUCCESSFULLY BOOTED UP!"
                tryBootText.alpha = 0f // start transparent
                tryBootText.visibility = View.VISIBLE
                tryBootText.animate()
                    .alpha(1f) // fade in to fully visible
                    .setDuration(800) // 0.8 seconds
                    .start()

                val checkIcon = findViewById<ImageView>(R.id.check_icon)
                checkIcon.scaleX = 0f
                checkIcon.scaleY = 0f
                checkIcon.visibility = View.VISIBLE
                checkIcon.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(500)
                    .setStartDelay(500) // start after text fades in
                    .start()


                partCase.postDelayed({
                    checkIcon.visibility = View.GONE
                    tryBootText.text = "LET'S SET UP THE PERIPHERALS"
                    nextButton.visibility = View.VISIBLE
                }, 1500)
            }
        }
    }


    private fun handleComputerPartClick(part: ImageView, installedImage: Int, index: Int) {
        if (currentStep >= correctCaseImages.size - 1) return
        userOrder.add(part.id)

        // Check if wrong flow triggered
        if (!wrongFlow && part.id != correctOrder[currentStep]) {
            wrongFlow = true
        }

        if (wrongFlow) {
            // Wrong flow: animate to random position on center image
            animatePartToCenterRandom(part)
        } else {
            // Correct flow: animate and change center image
            animatePartToCase(part, installedImage)
        }

        currentStep++

        if (currentStep == remainingParts.size) {
            if (wrongFlow) {
                // Wrong flow computer finished
                partCase.postDelayed({
                    tryBootText.text = "LET'S SET UP THE PERIPHERALS"
                    tryBootText.visibility = View.VISIBLE
                    nextButton.visibility = View.VISIBLE
                }, 800)
            } else {
                // Correct flow computer finished
                tryBootText.text = "SUCCESSFULLY BOOTED UP!"
                tryBootText.visibility = View.VISIBLE
                partCase.postDelayed({
                    tryBootText.text = "LET'S SET UP THE PERIPHERALS"
                    nextButton.visibility = View.VISIBLE
                }, 1000)
            }
        }
    }

    private fun animatePartToCase(part: ImageView, installedImage: Int) {
        val targetX = partCase.x + partCase.width / 2f - part.width / 2f
        val targetY = partCase.y + partCase.height / 4f
        part.animate()
            .x(targetX)
            .y(targetY)
            .setDuration(600)
            .withEndAction {
                partCase.setImageResource(installedImage)
                part.visibility = View.GONE
            }
            .start()
    }

    private fun animatePartToCenterRandom(part: ImageView) {
        // Random positions on center image
        val randX = partCase.x + Random.nextFloat() * (partCase.width - part.width)
        val randY = partCase.y + Random.nextFloat() * (partCase.height / 2f)
        part.animate()
            .x(randX)
            .y(randY)
            .setDuration(600)
            .withEndAction { part.visibility = View.VISIBLE }
            .start()
    }

    private fun showPeripheralParts() {
        currentPhase = 2
        tryBootText.visibility = View.GONE
        nextButton.visibility = View.GONE

        // Hide all remaining computer parts (especially for wrong flow)
        remainingParts.forEach { part ->
            part.visibility = View.GONE
        }

        // Reset computer case for peripherals
        partCase.setImageResource(R.drawable.cs_peri_table_1_1)

        // Add top margin for this first image
        val layoutParams = partCase.layoutParams as? androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
        layoutParams?.topMargin = 150 // Adjust this value in pixels as needed
        partCase.layoutParams = layoutParams

        val periParts = listOf(
            findViewById<ImageView>(R.id.peri_cpu),
            findViewById<ImageView>(R.id.peri_monitor),
            findViewById<ImageView>(R.id.peri_keyboard),
            findViewById<ImageView>(R.id.peri_mouse)
        )

        periParts.forEach { part ->
            part.visibility = View.VISIBLE

            part.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // save offset + original position
                        v.tag = Triple(
                            event.rawX - v.x,
                            event.rawY - v.y,
                            Pair(v.x, v.y)
                        )
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val (dx, dy, _) = v.tag as Triple<Float, Float, Pair<Float, Float>>
                        v.x = event.rawX - dx
                        v.y = event.rawY - dy
                    }

                    MotionEvent.ACTION_UP -> {
                        val (_, _, origPos) = v.tag as Triple<Float, Float, Pair<Float, Float>>

                        val caseRect = Rect()
                        partCase.getHitRect(caseRect)

                        val partRect = Rect(
                            v.x.toInt(),
                            v.y.toInt(),
                            (v.x + v.width).toInt(),
                            (v.y + v.height).toInt()
                        )

                        if (Rect.intersects(caseRect, partRect)) {
                            handlePeripheralDrop(v as ImageView)
                        } else {
                            // return to original spot
                            v.animate()
                                .x(origPos.first)
                                .y(origPos.second)
                                .setDuration(300)
                                .start()
                        }
                    }
                }
                true
            }
        }

    }

    private fun handlePeripheralDrop(part: ImageView) {
        if (periStep >= periImages.size - 1) return

        periUserOrder.add(part.id)

        // animate to table center
        partCase.setImageResource(periImages[periStep + 1])
        part.visibility = View.GONE
        periStep++

        if (periUserOrder.size == periCorrectOrder.size) {

            finalText1.text = "NICE SET UP!"
            finalText2.text = "LET'S TRY TO BOOT IT UP!"

            finalText1.visibility = View.VISIBLE
            finalText2.visibility = View.VISIBLE

            val manImage = findViewById<ImageView>(R.id.text_with_man)
            manImage.setImageResource(R.drawable.cs_peri_man)
            manImage.visibility = View.VISIBLE

            bootButton.visibility = View.VISIBLE
        }
    }

    private fun handleBootButton() {
        bootButton.visibility = View.GONE
        if (wrongFlow && currentPhase == 2) {

            // ❌ Hide success UI
            findViewById<TextView>(R.id.final_text_1).visibility = View.GONE
            findViewById<TextView>(R.id.final_text_2).visibility = View.GONE
            findViewById<ImageView>(R.id.text_with_man).visibility = View.GONE
            findViewById<ImageButton>(R.id.try_again_button).visibility = View.GONE
            findViewById<ImageButton>(R.id.home_button).visibility = View.GONE

            // ❌ Hide boot button
            bootButton.visibility = View.GONE

            // ✅ Show failed PC
            partCase.setImageResource(R.drawable.cs_peri_failed_part)

            val brokePc = findViewById<ImageView>(R.id.cs_broke_pc)
            brokePc.setImageResource(R.drawable.cs_broke_pc)
            brokePc.visibility = View.VISIBLE

            // ✅ Show "TRY AGAIN" text image
            val tryText = findViewById<ImageView>(R.id.cs_try_text)
            tryText.setImageResource(R.drawable.cs_try_text)
            tryText.visibility = View.VISIBLE

            // ✅ Try button → restart assembly
            val tryButton = findViewById<ImageButton>(R.id.cs_try_button)
            tryButton.visibility = View.VISIBLE
            tryButton.setOnClickListener {
                recreate()
            }

            // ✅ Explanation button → explanation page
            val explanationButton = findViewById<ImageButton>(R.id.peri_failed_explain_button)
            explanationButton.setImageResource(R.drawable.cs_explain_button)
            explanationButton.visibility = View.VISIBLE
            explanationButton.setOnClickListener {
                val intent = Intent(this, CSExplanation::class.java)
                startActivity(intent)
            }
        } else if (!wrongFlow && currentPhase == 2) {

            // Hide boot button
            bootButton.visibility = View.GONE

            partCase.setImageResource(R.drawable.cs_peri_booted_up)

            // Change texts
            finalText1.text = "BOOTED SUCCESSFULLY!"
            finalText2.text = "VERY GOOD STUDENT!"

            finalText1.visibility = View.VISIBLE
            finalText2.visibility = View.VISIBLE

            // Change man to trophy
            val manImage = findViewById<ImageView>(R.id.text_with_man)
            manImage.setImageResource(R.drawable.cs_man_trophy)
            manImage.visibility = View.VISIBLE

            // Show buttons below text
            findViewById<ImageButton>(R.id.try_again_button).visibility = View.VISIBLE
            findViewById<ImageButton>(R.id.home_button).visibility = View.VISIBLE
        }

    }
}
