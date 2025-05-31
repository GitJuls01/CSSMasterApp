package com.example.css

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GuessCSSMainGame : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_guess_cssmain_game)

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

        // 🔽 Choice click listeners
        val choiceA = findViewById<FrameLayout>(R.id.choice_a_container)
        val choiceB = findViewById<FrameLayout>(R.id.choice_b_container)
        val choiceC = findViewById<FrameLayout>(R.id.choice_c_container)
        val choiceD = findViewById<FrameLayout>(R.id.choice_d_container)

        val choiceClickListener = {
            val intent = Intent(this, GuessCSSAnswerPage::class.java)
            startActivity(intent)
        }

        choiceA.setOnClickListener { choiceClickListener() }
        choiceB.setOnClickListener { choiceClickListener() }
        choiceC.setOnClickListener { choiceClickListener() }
        choiceD.setOnClickListener { choiceClickListener() }
    }
}
