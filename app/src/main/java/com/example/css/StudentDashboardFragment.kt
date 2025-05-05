package com.example.css

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment

class StudentDashboardFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_student_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set OnClickListener to navigate to ModuleHardware
        view.findViewById<ImageButton>(R.id.hardware_card).setOnClickListener {
            startActivity(Intent(requireContext(), ModuleHardware::class.java))
        }

        // Set OnClickListener to navigate to ModuleSoftware
        view.findViewById<ImageButton>(R.id.software_card).setOnClickListener {
            startActivity(Intent(requireContext(), ModuleSoftware::class.java))
        }
    }
}
