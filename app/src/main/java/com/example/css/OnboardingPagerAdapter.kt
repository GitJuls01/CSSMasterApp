package com.example.css

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class OnboardingPagerAdapter : RecyclerView.Adapter<OnboardingPagerAdapter.OnboardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val layoutId = when (viewType) {
            0 -> R.layout.onboarding_page1
            else -> R.layout.onboarding_page2
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        // Nothing to bind for static layouts
    }

    override fun getItemCount(): Int = 2

    override fun getItemViewType(position: Int): Int = position

    class OnboardingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
