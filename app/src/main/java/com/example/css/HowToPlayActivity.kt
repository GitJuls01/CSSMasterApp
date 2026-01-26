package com.example.css

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HowToPlayActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: HowToPlayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_how_to_play)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton = findViewById<ImageButton>(R.id.back_button)

        backButton.setOnClickListener {
            val intent = Intent(this, ComputerSetup::class.java)
            startActivity(intent)
            finish() // closes HowToPlayActivity
        }

        viewPager = findViewById(R.id.how_to_play_pager)

        adapter = HowToPlayAdapter(
            onPlayClicked = {
                // Go to ComputerSetupPage2
                val intent = Intent(this@HowToPlayActivity, ComputerSetupPage2::class.java)
                startActivity(intent)
                finish()
            }
        )

        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = true // allows swipe
    }
}
