package com.example.digitrack.adapters

// CustomAdapter.kt
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

class AttendancesAdapter(
    private val attendancesList: List<Students>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<AttendancesAdapter.AttendanceViewHolder>() {

    inner class AttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStudentName: TextView = itemView.findViewById(R.id.tvAttendanceName)
        private val ivWeekI: ImageView = itemView.findViewById(R.id.ivWeekI)
        private val ivWeekII: ImageView = itemView.findViewById(R.id.ivWeekII)
        private val ivWeekIII: ImageView = itemView.findViewById(R.id.ivWeekIII)
        private val ivWeekIV: ImageView = itemView.findViewById(R.id.ivWeekIV)

        fun bind(attendance: Students) {
            tvStudentName.text = attendance.studentName

            if (2 == 2) {     // Jika bulan anak tersebut masuk sama dengan bulan hari ini
                if (attendance.studentAttendance!! >= 4) {
                    ivWeekI.setColorFilter(ContextCompat.getColor(itemView.context, R.color.purple))
                    ivWeekII.setColorFilter(ContextCompat.getColor(itemView.context, R.color.purple))
                    ivWeekIII.setColorFilter(ContextCompat.getColor(itemView.context, R.color.purple))
                    ivWeekIV.setColorFilter(ContextCompat.getColor(itemView.context, R.color.purple))
                } else {
                    ivWeekI.setColorFilter(ContextCompat.getColor(itemView.context, R.color.purple))
                    ivWeekII.setColorFilter(ContextCompat.getColor(itemView.context, R.color.purple))
                    ivWeekIII.setColorFilter(ContextCompat.getColor(itemView.context, R.color.white))
                    ivWeekIV.setColorFilter(ContextCompat.getColor(itemView.context, R.color.white))
                }
            }

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