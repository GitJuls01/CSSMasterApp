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
