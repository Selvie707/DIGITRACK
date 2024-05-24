package com.example.digitrack.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.R
import com.example.digitrack.data.Levels
import com.example.digitrack.data.Materials
import com.example.digitrack.data.Students

class DailyReportAdapter(
    private val dailyReportList: List<Students>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<DailyReportAdapter.DailyReportViewHolder>() {

    inner class DailyReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        private val tvStudentLevel: TextView = itemView.findViewById(R.id.tvStudentLevel)

        fun bind(dailyReport: Students) {
            tvStudentName.text = dailyReport.studentName
            tvStudentLevel.text = dailyReport.levelId

            itemView.setOnClickListener {
                onItemClick(adapterPosition)
                val url = dailyReport.studentDailyReportLink
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyReportViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_daily_report, parent, false)
        return DailyReportViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dailyReportList.size
    }

    override fun onBindViewHolder(holder: DailyReportViewHolder, position: Int) {
        holder.bind(dailyReportList[position])
    }
}