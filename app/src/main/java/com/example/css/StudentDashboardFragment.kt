package com.example.css

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class StudentDashboardFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var progressHardware: TextView
    private lateinit var progressSoftware: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_student_dashboard, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        progressHardware = view.findViewById(R.id.progress_hardware)
        progressSoftware = view.findViewById(R.id.progress_software)


        // Load and display progress
        fetchUserProgress()
        // Set OnClickListener to navigate to ModuleHardware
        view.findViewById<ImageButton>(R.id.hardware_card).setOnClickListener {
            startActivity(Intent(requireContext(), ModuleHardware::class.java))
        }

        // Set OnClickListener to navigate to ModuleSoftware
        view.findViewById<ImageButton>(R.id.software_card).setOnClickListener {
            startActivity(Intent(requireContext(), ModuleSoftware::class.java))
        }
    }

    private fun fetchUserProgress() {
        val sharedPreferences = requireContext().getSharedPreferences("user_data", android.content.Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("name", "Default Name") ?: "Default Name"

        firestore.collection("user_progress_hw")
            .document(userName)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val q1 = document.getLong("1") ?: 0
                    val q2 = document.getLong("2") ?: 0
                    val q3 = document.getLong("3") ?: 0
                    val q4 = document.getLong("4") ?: 0
                    val q5 = document.getLong("5") ?: 0
                    val q6 = document.getLong("6") ?: 0

                    val total = q1 + q2 + q3 + q4 + q5 + q6
                    progressHardware.text = "Hardware: $total/6"
                }
            }
            .addOnFailureListener {
            }

        firestore.collection("user_progress_sw")
            .document(userName)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val q1 = document.getLong("1") ?: 0
                    val q2 = document.getLong("2") ?: 0
                    val q3 = document.getLong("3") ?: 0
                    val q4 = document.getLong("4") ?: 0
                    val q5 = document.getLong("5") ?: 0
                    val q6 = document.getLong("6") ?: 0

                    val total = q1 + q2 + q3 + q4 + q5 + q6
                    progressSoftware.text = "Software: $total/6"

                }
            }
            .addOnFailureListener {
            }
    }
}

