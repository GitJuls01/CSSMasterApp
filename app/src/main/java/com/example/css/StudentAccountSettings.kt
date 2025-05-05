package com.example.css

import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class StudentAccountSettings : Fragment() {

    private lateinit var logoutButton: ImageView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_student_account_settings, container, false)

        // Access SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("user_data", MODE_PRIVATE)

        val userName = sharedPreferences.getString("name", "Default Name") // Default name if not found
        val userEmail = sharedPreferences.getString("email", "Default Email") // Default email if not found

        // Display them in TextViews
        val nameTextView = view?.findViewById<TextView>(R.id.text_name)
        val emailTextView = view?.findViewById<TextView>(R.id.text_email)

        nameTextView?.text = getString(R.string.name_label, userName)
        emailTextView?.text = getString(R.string.email_label, userEmail)


        // Initialize backButton
        logoutButton = view.findViewById(R.id.button_logout)

        // Set up the button click listener for logout
        logoutButton.setOnClickListener {
            showConfirmLogout()
        }

        return view
    }

    private fun showConfirmLogout() {
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to log out?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            Toast.makeText(requireContext(), "Logging out...", Toast.LENGTH_SHORT).show()

            // Add delay before starting logout
            android.os.Handler(requireContext().mainLooper).postDelayed({
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
        val darkGreen = requireContext().getColor(R.color.dark_green) // Make sure this color exists

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
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()

        // Redirect the user to the login screen
        val intent = Intent(requireActivity(), ChooseUserLogin::class.java)
        startActivity(intent)
        requireActivity().finish() // Finish the current activity to prevent back navigation
    }
}
