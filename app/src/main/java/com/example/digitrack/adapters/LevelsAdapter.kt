package com.example.digitrack.adapters

// CustomAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.R
import com.example.digitrack.data.Levels
import com.example.digitrack.data.Materials

class LevelsAdapter(
    private val levelsList: List<Levels>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<LevelsAdapter.LevelViewHolder>() {

    inner class LevelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvLevelName: TextView = itemView.findViewById(R.id.tvLevel)

        fun bind(level: Levels) {
            tvLevelName.text = level.levelName
            itemView.setOnClickListener { onItemClick(adapterPosition) }
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