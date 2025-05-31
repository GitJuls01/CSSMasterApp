package com.example.css

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var backPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

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

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val openFragment = intent.getStringExtra("openFragment")

        // Set initial fragment
        if (savedInstanceState == null) {
            when (openFragment) {
                "studentDashboard" -> replaceFragment(StudentDashboardFragment())
                "games" -> replaceFragment(GamesFragment())
//                "tutorials" -> replaceFragment(TutorialsFragment())
                "account" -> replaceFragment(StudentAccountSettings())
                else -> replaceFragment(StudentDashboardFragment())
            }
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_modules -> replaceFragment(StudentDashboardFragment())
                R.id.nav_games -> replaceFragment(GamesFragment())
//                R.id.nav_tutorials -> replaceFragment(TutorialsFragment())
                R.id.nav_account -> replaceFragment(StudentAccountSettings())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

}
