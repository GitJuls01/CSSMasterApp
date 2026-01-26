package com.example.css

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CSHowToPreventBsod : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cs_how_to_prevent_bsod)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backToExplainButton = findViewById<ImageButton>(R.id.back_button)
        backToExplainButton.setOnClickListener {
            finish()
        }


        val backHomeButton = findViewById<ImageButton>(R.id.cs_backhome_button)
        backHomeButton.setOnClickListener {
            startActivity(Intent(this, ComputerSetup::class.java))
            finish()
        }
    }
}