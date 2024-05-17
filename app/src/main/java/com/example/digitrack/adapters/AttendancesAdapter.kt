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
import com.example.digitrack.data.Students

class AttendancesAdapter(
    private val attendancesList: List<Students>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<AttendancesAdapter.AttendanceViewHolder>() {

    inner class AttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStudentName: TextView = itemView.findViewById(R.id.tvAttendanceName)

        fun bind(attendance: Students) {
            tvStudentName.text = attendance.studentName
            itemView.setOnClickListener { onItemClick(adapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_attendance, parent, false)
        return AttendanceViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return attendancesList.size
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        holder.bind(attendancesList[position])
    }
}