package com.example.css

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class AuthenticationCode : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_authentication_code)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ✅ Initialize Firestore here
        firestore = FirebaseFirestore.getInstance()

        val submitButton = findViewById<ImageButton>(R.id.submit_button)
            submitButton.setOnClickListener {
                //saveUserToFirestore()
            }
    }

//    private fun saveUserToFirestore() {
//
//        val name = intent.getStringExtra("name") ?: ""
//        val section = intent.getStringExtra("section") ?: ""
//        val email = intent.getStringExtra("email") ?: ""
//        val password = intent.getStringExtra("password") ?: ""
//        val role = intent.getStringExtra("role") ?: ""
//
//        if (name.isEmpty() || section.isEmpty() || email.isEmpty() || password.isEmpty()) {
//            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
//            return
//        }
//    }
}