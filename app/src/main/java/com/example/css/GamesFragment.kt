package com.example.css

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
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

        val btnCompSetup = view.findViewById<ImageButton>(R.id.btn_comp_setup)
        btnCompSetup.setOnClickListener {
            // Create an Intent to start ComputerSetup activity
            val intent = Intent(requireContext(), ComputerSetup::class.java)
            startActivity(intent)
        }

        val btnQuizTime = view.findViewById<ImageButton>(R.id.btn_quiz_time)
        btnQuizTime.setOnClickListener {
            showCountdownDialog{
                val intent = Intent(requireContext(), GuessCSSMainGame::class.java)
                startActivity(intent)
            }
        }

//        val btnGuessCss = view.findViewById<ImageButton>(R.id.btn_guess_css)
//        btnGuessCss.setOnClickListener {
//            val intent = Intent(requireContext(), GuessCSSMainGame::class.java)
//            startActivity(intent)
//        }
        return view
    }

    private fun showCountdownDialog(onFinish: () -> Unit) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.loading_quiz_countdown, null)
        val countdownText = dialogView.findViewById<TextView>(R.id.countdown_text)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Get Ready!")
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.show()

        object : CountDownTimer(6000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                countdownText.text = "The quiz will start in $secondsLeft..."
            }

            override fun onFinish() {
                dialog.dismiss()
                onFinish()
            }
        }.start()
    }

}
