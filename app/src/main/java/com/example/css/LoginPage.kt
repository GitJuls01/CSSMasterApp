package com.example.css

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore


class LoginPage : AppCompatActivity() {

    private var backPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_page)
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

        // Find the teacher_button by ID
        val backButton = findViewById<ImageButton>(R.id.back_button)
        val logo = findViewById<ImageView>(R.id.logo)
        val loginButton = findViewById<ImageButton>(R.id.login_button)

        // Get userType from Intent
        val userType = intent.getStringExtra("userType")

        // Set logo based on userType
        if (userType == "teacher") {
            logo.setImageResource(R.drawable.teacher_logo) // Set teacher logo
        } else if (userType == "student") {
            logo.setImageResource(R.drawable.student_logo) // Set student logo
        }

        // Set OnClickListener to navigate to LoginPage
        backButton.setOnClickListener {
            val intent = Intent(this, ChooseUserLogin::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            login()

        }
    }
    private fun login() {
        val emailInput = findViewById<EditText>(R.id.input_email)
        val passwordInput = findViewById<EditText>(R.id.input_password)
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val userDoc = documents.documents[0]
                    val storedPassword = userDoc.getString("password")

                    if (storedPassword == password) {
                        // Redirect to appropriate dashboard based on role
                        val role = userDoc.getString("role")
                        val name = userDoc.getString("name")
                        if (role == "teacher") {
                            val intent = Intent(this, TeacherDashboard::class.java)
                            intent.putExtra("userId", userDoc.id)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("openFragment", "studentDashboard")
                            intent.putExtra("userId", userDoc.id)
                            startActivity(intent)
                            finish()
                        }
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                        // Save session data using SharedPreferences
                        val sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putBoolean("is_logged_in", true)
                            putString("role", role)
                            putString("name", name)
                            putString("email", email)
                            putString("userId", userDoc.id)

                            apply()
                        }
                    } else {
                        Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}