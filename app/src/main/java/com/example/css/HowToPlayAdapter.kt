package com.example.css

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HowToPlayAdapter(
    private val onPlayClicked: () -> Unit
) : RecyclerView.Adapter<HowToPlayAdapter.SlideViewHolder>() {

    private val slidesCount = 5

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_how_to_play_slide, parent, false)
        return SlideViewHolder(view)
    }

    override fun getItemCount(): Int = slidesCount

    override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class SlideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val centerImage: ImageView = itemView.findViewById(R.id.center_image)
        private val instructionText: ImageView = itemView.findViewById(R.id.instruction_text)
        private val part1: ImageView = itemView.findViewById(R.id.part1)
        private val part2: ImageView = itemView.findViewById(R.id.part2)
        private val part3: ImageView = itemView.findViewById(R.id.part3)
        private val part4: ImageView = itemView.findViewById(R.id.part4)
        private val part5: ImageView = itemView.findViewById(R.id.part5)
        private val explanationText: TextView = itemView.findViewById(R.id.explanation_text)
        private val extraArrow: ImageView = itemView.findViewById(R.id.extra_arrow)
        private val brokePc: ImageView = itemView.findViewById(R.id.broke_pc)
        private val wrongInputText: TextView = itemView.findViewById(R.id.wrong_input_text)
        private val successText: TextView = itemView.findViewById(R.id.success_text)
        private val failedText: TextView = itemView.findViewById(R.id.failed_text)
        private val explainButton: ImageView = itemView.findViewById(R.id.explain_button)
        private val manTrophy: ImageView = itemView.findViewById(R.id.man_trophy)
        private val playButton: Button = itemView.findViewById(R.id.play_button)

        // Flag to control the looping animation
        private var isSlide1AnimationRunning = false

        fun bind(position: Int) {
            resetCenterImageSize()

            setCenterImageTopMargin(0) // default for Slide 1 & 2

            // Hide optional views first
            explanationText.visibility = View.GONE
            extraArrow.visibility = View.GONE
            brokePc.visibility = View.GONE
            part1.visibility = View.GONE
            part2.visibility = View.GONE
            part3.visibility = View.GONE
            part4.visibility = View.GONE
            part5.visibility = View.GONE
            wrongInputText.visibility = View.GONE
            successText.visibility = View.GONE
            failedText.visibility = View.GONE
            explainButton.visibility = View.GONE
            manTrophy.visibility = View.GONE
            playButton.visibility = View.GONE

            when (position) {
                0 -> startSlide1()
                1 -> startSlide2()
                2 -> startSlide3()
                3 -> startSlide4()
                4 -> startSlide5()
            }
        }

        private fun setCenterImageSize(dpWidth: Int, dpHeight: Int) {
            val params = centerImage.layoutParams
            val scale = itemView.resources.displayMetrics.density

            params.width = (dpWidth * scale).toInt()
            params.height = (dpHeight * scale).toInt()

            centerImage.layoutParams = params
        }

        private fun resetCenterImageSize() {
            val params = centerImage.layoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            centerImage.layoutParams = params
        }

        private fun setCenterImageTopMargin(dp: Int) {
            val params = centerImage.layoutParams as ViewGroup.MarginLayoutParams
            val scale = itemView.resources.displayMetrics.density
            params.topMargin = (dp * scale).toInt()
            centerImage.layoutParams = params
        }

        // -----------------------------
        // Slide 1: Looping animation
        // -----------------------------
        private fun startSlide1() {
            isSlide1AnimationRunning = true

            // Show parts
            part1.setImageResource(R.drawable.cs_motherboard_with_arrow)
            part2.setImageResource(R.drawable.cs_cpu_1_3)
            part3.setImageResource(R.drawable.cs_cpu_fan_1_4)
            part4.setImageResource(R.drawable.cs_ram_1_5)
            part5.setImageResource(R.drawable.cs_hdd_1_6)

            part1.visibility = View.VISIBLE
            part2.visibility = View.VISIBLE
            part3.visibility = View.VISIBLE
            part4.visibility = View.VISIBLE
            part5.visibility = View.VISIBLE

            // Start the loop
            loopSlide1()
        }

        private fun loopSlide1() {
            if (!isSlide1AnimationRunning) return

            setCenterImageSize(
                dpWidth = 300,
                dpHeight = 300
            )

            // Reset initial state
            centerImage.setImageResource(R.drawable.cs_comp_case_2_1)
            instructionText.setImageResource(R.drawable.cs_drag_instruction)
            part1.translationX = 0f
            part1.translationY = 0f
            part1.visibility = View.VISIBLE

            part1.post {
                val centerX = centerImage.x + centerImage.width / 2f - part1.width / 2f
                val centerY = centerImage.y + centerImage.height / 2f - part1.height / 2f
                val deltaX = centerX - part1.x
                val deltaY = centerY - part1.y

                val animatorX = ObjectAnimator.ofFloat(part1, View.TRANSLATION_X, 0f, deltaX)
                val animatorY = ObjectAnimator.ofFloat(part1, View.TRANSLATION_Y, 0f, deltaY)

                AnimatorSet().apply {
                    playTogether(animatorX, animatorY)
                    duration = 1200
                    start()
                    doOnEnd {
                        // Step 1: Show drop result
                        centerImage.setImageResource(R.drawable.cs_case_with_arrow)
                        instructionText.setImageResource(R.drawable.cs_drop_instruction)

                        // Step 2: Hide motherboard after it is "dropped"
                        part1.visibility = View.GONE

                        // Step 3: Delay before restarting loop
                        part1.postDelayed({
                            if (!isSlide1AnimationRunning) return@postDelayed

                            // Reset to drag state
                            centerImage.setImageResource(R.drawable.cs_comp_case_2_1)
                            instructionText.setImageResource(R.drawable.cs_drag_instruction)

                            part1.translationX = 0f
                            part1.translationY = 0f
                            part1.visibility = View.VISIBLE

                            // Restart animation
                            loopSlide1()
                        }, 1000) // 👈 adjust delay (500–1000ms looks good)
                    }

                }
            }
        }

        // -----------------------------
        // Slide 2
        // -----------------------------
        private fun startSlide2() {
            isSlide1AnimationRunning = false // stop slide 1 loop

            setCenterImageSize(
                dpWidth = 300,
                dpHeight = 300
            )

            centerImage.setImageResource(R.drawable.cs_case_with_arrow)
            instructionText.setImageResource(R.drawable.cs_wrong_instruction)
            part1.setImageResource(R.drawable.cs_cpu_1_3)
            part2.setImageResource(R.drawable.cs_cpu_fan_1_4)
            part3.setImageResource(R.drawable.cs_ram_1_5)
            part4.setImageResource(R.drawable.cs_hdd_1_6)

            part1.visibility = View.VISIBLE
            part2.visibility = View.VISIBLE
            part3.visibility = View.VISIBLE
            part4.visibility = View.VISIBLE

            wrongInputText.visibility = View.VISIBLE
        }

        // -----------------------------
        // Slide 3
        // -----------------------------
        private fun startSlide3() {
            isSlide1AnimationRunning = false // stop slide 1 loop

            setCenterImageTopMargin(32)
            centerImage.setImageResource(R.drawable.cs_peri_booted_up)
            instructionText.visibility = View.GONE
            successText.visibility = View.VISIBLE
            manTrophy.visibility = View.VISIBLE
        }

        // -----------------------------
        // Slide 4
        // -----------------------------
        private fun startSlide4() {
            isSlide1AnimationRunning = false // stop slide 1 loop
            setCenterImageTopMargin(32)

            centerImage.setImageResource(R.drawable.cs_peri_failed_boot)
            instructionText.visibility = View.GONE
            failedText.visibility = View.VISIBLE
            explanationText.visibility = View.VISIBLE
            explainButton.visibility = View.VISIBLE
            extraArrow.visibility = View.VISIBLE
            brokePc.visibility = View.VISIBLE

            explainButton.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, CSExplanation::class.java)
                context.startActivity(intent)
            }
        }

        // -----------------------------
        // Slide 5: Ready to Play
        // -----------------------------
        private fun startSlide5() {
            isSlide1AnimationRunning = false

            setCenterImageTopMargin(32)
            // 👇 Reduce size here
            setCenterImageSize(
                dpWidth = 400,
                dpHeight = 400
            )
            centerImage.setImageResource(R.drawable.cs_ready)
            instructionText.visibility = View.GONE

            playButton.visibility = View.VISIBLE

            playButton.setOnClickListener {
                onPlayClicked.invoke()
            }
        }

    }
}

// Extension function to detect end of animation
fun AnimatorSet.doOnEnd(onEnd: () -> Unit) {
    this.addListener(object : android.animation.AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: android.animation.Animator) {
            onEnd()
        }
    })
}
