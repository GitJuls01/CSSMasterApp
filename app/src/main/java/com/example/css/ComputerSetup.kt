package com.example.css

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import android.app.ActivityOptions

class ComputerSetup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_computer_setup)

        // Handle system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("openFragment", "games")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        val card = findViewById<MaterialCardView>(R.id.green_text_card)

        // Animate card expanding downward
        card.post {
            val fullHeight = card.height
            card.layoutParams.height = 0
            card.requestLayout()

            val anim = ValueAnimator.ofInt(0, fullHeight)
            anim.duration = 1200
            anim.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Int
                card.layoutParams.height = value
                card.requestLayout()
            }

            anim.start()
        }

        val playButton = findViewById<MaterialButton>(R.id.play_button)

        playButton.post {
            val fullWidth = playButton.width
            val fullHeight = playButton.height

            val clip = Rect(0, 0, 0, fullHeight)
            playButton.clipBounds = clip

            val anim = ValueAnimator.ofInt(0, fullWidth)
            anim.duration = 1200
            anim.addUpdateListener {
                clip.right = it.animatedValue as Int
                playButton.clipBounds = clip
            }
            anim.start()
        }

        playButton.setOnClickListener {
            val intent = Intent(this, ComputerSetupPage2::class.java)

            // Create slide-in animation from right
            val options = ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.slide_in_left,  // enter animation
                android.R.anim.slide_out_right // exit animation
            )

            startActivity(intent, options.toBundle())
        }

        val howToPlayButton = findViewById<MaterialButton>(R.id.how_to_play_button)

        // Initial state
        howToPlayButton.alpha = 0f
        howToPlayButton.translationY = 30f

        howToPlayButton.post {
            howToPlayButton.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(900) // appears AFTER play button animation
                .setInterpolator(android.view.animation.DecelerateInterpolator())
                .start()
        }

        howToPlayButton.setOnClickListener {
            val intent = Intent(this, HowToPlayActivity::class.java)
            startActivity(intent)
        }

    }
}
