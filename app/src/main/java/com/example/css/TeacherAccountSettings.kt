package com.example.css

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TeacherAccountSettings : AppCompatActivity() {

    private lateinit var logoutButton: ImageView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_account_settings)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Access SharedPreferences
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)

        val userName = sharedPreferences.getString("name", "Default Name")
        val userEmail = sharedPreferences.getString("email", "Default Email")
        val backBtn = findViewById<ImageButton>(R.id.back_button)

        // Display them in TextViews
        val nameTextView = findViewById<TextView>(R.id.text_name)
        val emailTextView = findViewById<TextView>(R.id.text_email)

        nameTextView.text = getString(R.string.name_label, userName)
        emailTextView.text = getString(R.string.email_label, userEmail)

        // Initialize logoutButton
        logoutButton = findViewById(R.id.button_logout)

        // Set up the button click listener for logout
        logoutButton.setOnClickListener {
            showConfirmLogout()
        }

        backBtn.setOnClickListener {
            finish() // Go back to the previous screen
        }
    }

    private fun showConfirmLogout() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to log out?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show()

            // Add delay before starting logout
            android.os.Handler(mainLooper).postDelayed({
                logout()
            }, 1500)

            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

        // Set button text color after dialog is shown
        val darkGreen = getColor(R.color.dark_green)

        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)?.setTextColor(darkGreen)
        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)?.setTextColor(darkGreen)
    }

    private fun logout() {
        // Clear session data stored in SharedPreferences
        with(sharedPreferences.edit()) {
            remove("is_logged_in")
            remove("role")
            remove("userId")
            apply()
        }

        // Optionally, show a logout success message
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

        // Redirect the user to the login screen
        val intent = Intent(this, ChooseUserLogin::class.java)
        startActivity(intent)
        finish() // Finish the current activity to prevent back navigation
    }
}
