package com.example.css

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment

class GamesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_games, container, false)

        val btnCompRepair = view.findViewById<ImageButton>(R.id.btn_comp_repair)
        btnCompRepair.setOnClickListener {
            // Create an Intent to start ComputerRepair activity
            val intent = Intent(requireContext(), ComputerRepair::class.java)
            startActivity(intent)
        }

        val btnQuizTime = view.findViewById<ImageButton>(R.id.btn_quiz_time)
        btnQuizTime.setOnClickListener {
            val intent = Intent(requireContext(), QuizTime::class.java)
            startActivity(intent)
        }

        return view
    }
}
