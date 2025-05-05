package com.example.css

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

class CreateAccount : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_account)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firestore = FirebaseFirestore.getInstance()

        val submitButton = findViewById<ImageButton>(R.id.submit_button)
        submitButton.setOnClickListener {
            val name = findViewById<EditText>(R.id.name_input).text.toString().trim()
            val section = findViewById<EditText>(R.id.section_input).text.toString().trim()
            val email = findViewById<EditText>(R.id.email_input).text.toString().trim()
            val password = findViewById<EditText>(R.id.password_input).text.toString().trim()
            val confirmPassword = findViewById<EditText>(R.id.confirm_password_input).text.toString().trim()
            val role = if (findViewById<RadioButton>(R.id.radio_teacher).isChecked) "teacher" else "student"

            if (name.isEmpty() || section.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!validateEmail()) {
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords does not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isPasswordStrong(password)) {
                Toast.makeText(this, "Password is too weak", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a user map
            val createdDate = Timestamp.now()
            val userMap = hashMapOf(
                "name" to name,
                "section" to section,
                "email" to email,
                "password" to password,
                "role" to role,
                "createdDate" to createdDate
            )

            firestore.collection("users")
                .add(userMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, LoginPage::class.java)
                    intent.putExtra("userType", role)
                    startActivity(intent)
                    finish()

                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        val loginButton = findViewById<ImageButton>(R.id.login_button)
        loginButton.setOnClickListener {
            val intent = Intent(this, ChooseUserLogin::class.java)
            startActivity(intent)
        }

        val radioTeacher = findViewById<RadioButton>(R.id.radio_teacher)
        val radioStudent = findViewById<RadioButton>(R.id.radio_student)

        radioTeacher.buttonTintList = ContextCompat.getColorStateList(this, R.color.green_checked)
        radioStudent.buttonTintList = ContextCompat.getColorStateList(this, R.color.green_checked)

        setupPasswordValidation()
        setupPasswordMatchValidation()
    }

    // Call this inside onCreate()
    private fun setupPasswordValidation() {
        val passwordInput = findViewById<EditText>(R.id.password_input)
        val warningText = findViewById<TextView>(R.id.password_warning)

        passwordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                if (isPasswordStrong(password)) {
                    warningText.visibility = TextView.GONE
                    //setEditTextBorderColor(passwordInput, Color.parseColor("#3E7B27")) // green
                    //passwordInput.setTextColor(Color.BLACK) // black text
                } else {
                    warningText.visibility = TextView.VISIBLE
                    //setEditTextBorderColor(passwordInput, Color.parseColor("#D84040")) // red
                    //passwordInput.setTextColor(Color.WHITE) // white text
                }
            }
        })
    }

    // Basic strong password check: at least 1 letter, 1 number, 1 special char, 6+ chars
    private fun isPasswordStrong(password: String): Boolean {
        val letter = Regex(".*[a-zA-Z].*")
        val number = Regex(".*[0-9].*")
        val special = Regex(".*[!@#\$%^&*(),.?\":{}|<>].*")
        return password.length >= 6 && password.matches(letter) && password.matches(number) && password.matches(special)
    }

    // Change EditText border color (assuming text_field_container has a shape drawable)
    private fun setEditTextBorderColor(editText: EditText, color: Int) {
        val background = editText.background.mutate()
        if (background is GradientDrawable) {
            background.setStroke(3, color)
        } else {
            editText.background.setTint(color) // fallback
        }
    }

    private fun setupPasswordMatchValidation() {
        val passwordInput = findViewById<EditText>(R.id.password_input)
        val confirmPasswordInput = findViewById<EditText>(R.id.confirm_password_input)
        val warningText = findViewById<TextView>(R.id.confirm_password_warning)

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val password = passwordInput.text.toString()
                val confirmPassword = confirmPasswordInput.text.toString()

                if (confirmPassword.isEmpty()) {
                    confirmPasswordInput.setBackgroundResource(R.drawable.text_field_container)
                } else if (confirmPassword == password) {
                    warningText.visibility = TextView.GONE
                    //setEditTextBorderColor(confirmPasswordInput, Color.parseColor("#00C853")) // green
                    //confirmPasswordInput.setTextColor(Color.BLACK) // white text
                } else {
                    warningText.visibility = TextView.VISIBLE
                    //setEditTextBorderColor(confirmPasswordInput, Color.parseColor("#D50000")) // red
                    //confirmPasswordInput.setTextColor(Color.WHITE) // white text
                }
            }
        }

        passwordInput.addTextChangedListener(watcher)
        confirmPasswordInput.addTextChangedListener(watcher)
    }

    private fun validateEmail(): Boolean {
        val emailInput = findViewById<EditText>(R.id.email_input)
        val email = emailInput.text.toString().trim()

        return if (email.isEmpty()) {
            emailInput.error = "Email is required"
            Toast.makeText(applicationContext, "Email is required", Toast.LENGTH_SHORT).show()

            false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.error = "Enter a valid email address"
            Toast.makeText(applicationContext, "Enter a valid email address", Toast.LENGTH_SHORT).show()

            false
        } else {
            emailInput.error = null // clear error
            true
        }
    }

}