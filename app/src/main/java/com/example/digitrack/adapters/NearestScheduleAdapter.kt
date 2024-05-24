package com.example.digitrack.adapters

import android.content.Intent
import android.net.Uri
import android.util.Log
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
import java.time.DayOfWeek
import java.time.LocalDate

class NearestScheduleAdapter(
    private val scheduleList: List<Students>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<NearestScheduleAdapter.ScheduleViewHolder>() {

    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStudentName: TextView = itemView.findViewById(R.id.tvNamaAnak)
        private val tvStudentLevel: TextView = itemView.findViewById(R.id.tvLevelAnak)
        private val tvStudentWeek: TextView = itemView.findViewById(R.id.tvMingguKe)
        private val tvStudentMaterial: TextView = itemView.findViewById(R.id.tvMateriAnak)

        fun bind(schedule: Students) {
            val studentWeek = schedule.studentAttendanceMaterials.keys.firstOrNull().toString()

            tvStudentName.text = schedule.studentName
            tvStudentLevel.text = schedule.levelId
            tvStudentWeek.text = "Week $studentWeek"
            tvStudentMaterial.text = schedule.studentAttendanceMaterials.values.firstOrNull().toString()

            itemView.setOnClickListener {
                onItemClick(adapterPosition)
                val url = schedule.studentDailyReportLink
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row_nearestschedule, parent, false)
        return ScheduleViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return scheduleList.size
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(scheduleList[position])
    }
}