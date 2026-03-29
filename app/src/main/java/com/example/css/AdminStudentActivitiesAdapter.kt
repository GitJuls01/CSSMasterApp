package com.example.css

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.css.AdminStudentLeaderboardDetailsItem

class AdminStudentActivitiesAdapter(private val list: List<AdminStudentActivitiesDetailsItem>) :
    RecyclerView.Adapter<AdminStudentActivitiesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val numberStatus: TextView = view.findViewById(R.id.tv_number_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.admin_student_activities_details_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        // Set text
        holder.numberStatus.text = "${item.quizNumber} - ${item.status}"

    }
}