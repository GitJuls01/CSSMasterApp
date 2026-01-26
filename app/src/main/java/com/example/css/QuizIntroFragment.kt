package com.example.css

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton

class QuizIntroFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quiz_intro, container, false)

        val playButton = view.findViewById<MaterialButton>(R.id.play_button)
        playButton.setOnClickListener {
            // Replace current fragment with QuizTime fragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, QuizTime()) // Make sure R.id.fragment_container exists in your activity
                .addToBackStack(null) // Optional: allows user to press back to return
                .commit()
        }

        return view
    }
}
