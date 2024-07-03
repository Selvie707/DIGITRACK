package com.example.digitrack.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.R
import com.example.digitrack.data.Levels

class LevelsAdapter(
    private val levelsList: List<Levels>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<LevelsAdapter.LevelViewHolder>() {

    private var selectedPosition = 0

    inner class LevelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvLevelName: TextView = itemView.findViewById(R.id.tvLevel)

        fun bind(level: Levels) {
            tvLevelName.text = level.levelName

            if (position == selectedPosition) {
                tvLevelName.textSize = 23f
                tvLevelName.setTypeface(null, android.graphics.Typeface.BOLD)
            } else {
                tvLevelName.textSize = 20f
                tvLevelName.setTypeface(null, android.graphics.Typeface.NORMAL)
            }

            itemView.setOnClickListener {
                notifyItemChanged(selectedPosition)
                selectedPosition = adapterPosition
                notifyItemChanged(selectedPosition)
                onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_level, parent, false)
        return LevelViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return levelsList.size
    }

    override fun onBindViewHolder(holder: LevelViewHolder, position: Int) {
        holder.bind(levelsList[position])
    }
}