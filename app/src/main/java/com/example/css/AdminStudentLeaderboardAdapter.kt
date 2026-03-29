package com.example.css

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.css.AdminStudentLeaderboardDetailsItem

class AdminStudentLeaderboardAdapter(private val list: List<AdminStudentLeaderboardDetailsItem>) :
    RecyclerView.Adapter<AdminStudentLeaderboardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val medal: ImageView = view.findViewById(R.id.img_medal)
        val nameScore: TextView = view.findViewById(R.id.tv_name_score)
        val attempts: TextView = view.findViewById(R.id.tv_attempts)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.admin_student_leaderboard_details_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        // Set text
        holder.nameScore.text = "${item.score}% - ${item.name}"
        holder.attempts.text = "${item.attempts} Attempts"

        // Use ONE medal for all
        holder.medal.setImageResource(R.drawable.admin_leaderboard_medal)
    }
}