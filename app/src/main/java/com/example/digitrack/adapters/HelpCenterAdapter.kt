package com.example.digitrack.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.R
import com.example.digitrack.data.HelpCenter
import com.example.digitrack.data.Levels
import com.example.digitrack.data.Materials
import com.example.digitrack.data.Students

class HelpCenterAdapter(
    private val hcList: List<HelpCenter>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<HelpCenterAdapter.HelpCenterViewHolder>() {

    inner class HelpCenterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvQuestion: TextView = itemView.findViewById(R.id.tvQuestion)
        private val tvAnswer: TextView = itemView.findViewById(R.id.tvAnswer)

        fun bind(helpCenter: HelpCenter) {
            tvQuestion.text = helpCenter.hcQuestion
            tvAnswer.text = helpCenter.hcAnswer

            itemView.setOnClickListener { onItemClick(adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpCenterViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_faq, parent, false)
        return HelpCenterViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return hcList.size
    }

    override fun onBindViewHolder(holder: HelpCenterViewHolder, position: Int) {
        holder.bind(hcList[position])
    }
}