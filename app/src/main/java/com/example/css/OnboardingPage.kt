package com.example.css

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class OnboardingPage : AppCompatActivity() {

    private var backPressedOnce = false

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboarding_page)

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

        // Initialize shared preferences and check login status
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        val role = sharedPreferences.getString("role", "")
        with(sharedPreferences.edit()) {
            putString("role", role)
            apply()
        }

        if (isLoggedIn) {
            if (role == "student") {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                return
            }
            else {
                val intent = Intent(this, TeacherDashboard::class.java)
                startActivity(intent)
                finish()
                return
            }
        }

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val onboardingPagerAdapter = OnboardingPagerAdapter()
        viewPager.adapter = onboardingPagerAdapter

        // Set up page change listener for updating the dots
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updatePageDots(position)
            }
        })

        // Set up Get Started button
        val getStartedButton = findViewById<ImageButton>(R.id.getStartedButton)
        getStartedButton.setOnClickListener {
            val intent = Intent(this, ChooseUserLogin::class.java)
            startActivity(intent)
        }
    }

    // Dynamically update dots based on the page index
    private fun updatePageDots(position: Int) {
        val circleTabs = findViewById<LinearLayout>(R.id.circleTabs)
        val totalDots = circleTabs.childCount

        for (i in 0 until totalDots) {
            val dot = circleTabs.getChildAt(i)
            dot.setBackgroundResource(
                if (i == position) R.drawable.get_started_selected_dot else R.drawable.get_started_unselected_dot
            )
        }
    }
}
