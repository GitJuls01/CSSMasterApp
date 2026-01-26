package com.example.css

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BottomNav : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupBottomNavigation()
    }

    protected abstract fun getLayoutResourceId(): Int

    fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNav?.setOnItemSelectedListener { item -> // Use ?. to prevent null crash
            val currentActivity = this::class.java

            when (item.itemId) {
                R.id.nav_modules -> {
                    if (currentActivity != StudentDashboardFragment::class.java) {
                        startActivity(Intent(this, StudentDashboardFragment::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_games -> {
                    if (currentActivity != GamesFragment::class.java) {
                        startActivity(Intent(this, GamesFragment::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_quizzes -> {
                    if (currentActivity != QuizIntroFragment::class.java) {
                        startActivity(Intent(this, QuizIntroFragment::class.java))
                        finish()
                    }
                    true
                }
//                R.id.nav_tutorials -> {
//                    if (currentActivity != TutorialsFragment::class.java) {
//                        startActivity(Intent(this, TutorialsFragment::class.java))
//                        finish()
//                    }
//                    true
//                }
                R.id.nav_account -> {
                    if (currentActivity != StudentAccountSettings::class.java) {
                        startActivity(Intent(this, StudentAccountSettings::class.java))
                        finish()
                    }
                    true
                }
                else -> false
            }
        }
    }
}

