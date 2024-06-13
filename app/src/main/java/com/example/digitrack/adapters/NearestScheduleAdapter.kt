package com.example.digitrack.adapters

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.R
import com.example.digitrack.data.Students
import com.google.firebase.firestore.FirebaseFirestore

class NearestScheduleAdapter(
    private val scheduleList: List<Students>,
    private val selectedDate: String, // Tambahkan parameter ini
    private val selectedTime: String, // Tambahkan parameter ini
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<NearestScheduleAdapter.ScheduleViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    var check = false

    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStudentName: TextView = itemView.findViewById(R.id.tvNamaAnak)
        private val tvStudentLevel: TextView = itemView.findViewById(R.id.tvLevelAnak)
        private val tvStudentWeek: TextView = itemView.findViewById(R.id.tvMingguKe)
        private val tvStudentMaterial: TextView = itemView.findViewById(R.id.tvMateriAnak)
        private val cbAttendance: CheckBox = itemView.findViewById(R.id.cbNearestSchedule)

        fun bind(schedule: Students) {
            val studentWeek = schedule.studentAttendanceMaterials.keys.firstOrNull().toString()
            val keySchedule = schedule.studentSchedule.keys.find { key ->
                schedule.studentSchedule[key] == "$selectedDate|$selectedTime"
            }

            tvStudentName.text = schedule.studentName
            tvStudentLevel.text = schedule.levelId
            tvStudentWeek.text = "Week $keySchedule"
            tvStudentMaterial.text = schedule.studentAttendanceMaterials[keySchedule] ?: "No material"

            cbAttendance.setOnClickListener {
                if (cbAttendance.isChecked) {
                    // Update studentAttendance in Firestore
                    val b = cbAttendance.isChecked
                    println("a: $b")
                    check = true
                    val newAttendanceCount = (schedule.studentAttendance ?: 0) + 1
                    db.collection("student")
                        .document(schedule.studentId)
                        .update("studentAttendance", newAttendanceCount)
                        .addOnSuccessListener {
                            Log.d("NearestScheduleAdapter", "Attendance updated successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("NearestScheduleAdapter", "Error updating attendance", e)
                        }
                } else if (!cbAttendance.isChecked && check) {
                    // Update studentAttendance in Firestore
                    val a = !cbAttendance.isChecked
                    println("$a $check")
                    check = false
                    val newAttendanceCount = (schedule.studentAttendance ?: 0)
                    db.collection("student")
                        .document(schedule.studentId)
                        .update("studentAttendance", newAttendanceCount)
                        .addOnSuccessListener {
                            Log.d("NearestScheduleAdapter", "Attendance updated successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("NearestScheduleAdapter", "Error updating attendance", e)
                        }
                }
            }

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