package com.example.css

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextView
import android.view.ViewGroup.LayoutParams
import android.view.Gravity
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ComputerRepair : AppCompatActivity() {

    private var backPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_computer_repair)

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

        val playButton = findViewById<ImageButton>(R.id.play_button)
        playButton.setOnClickListener {
            val intent = Intent(this, ComputerRepairMainGame::class.java)
            startActivity(intent)
        }

        val welcomeText = findViewById<TextView>(R.id.welcome_text)

        val showCompletion = intent.getBooleanExtra("showCompletion", false)

        if (showCompletion) {
            // Change text to final congratulations message (with bold style)
            welcomeText.text = HtmlCompat.fromHtml(
                "Congratulations - you've officially built<br>" +
                        "your own PC! Every part you connected,<br>" +
                        "every cable you managed, and every<br>" +
                        "choice you made has come together to<br>" +
                        "create a machine that's 100% yours.",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            // Change play button image
            playButton.setImageResource(R.drawable.cp_play_again_button)
        }
    }

    private fun showExitDialog() {
        val dialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_exit_game, null)

        val btnYes = view.findViewById<ImageButton>(R.id.btn_yes)
        val btnNo = view.findViewById<ImageButton>(R.id.btn_no)

        btnYes.setOnClickListener {
            dialog.dismiss()
            val showCompletion = intent.getBooleanExtra("showCompletion", false)
            if (showCompletion) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("openFragment", "games")
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            } else {
                finish()
            }

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
