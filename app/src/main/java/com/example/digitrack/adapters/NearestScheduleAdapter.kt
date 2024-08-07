package com.example.digitrack.adapters

import android.content.Context
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
    private val selectedDate: String,
    private val selectedTime: String,
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
            val context = itemView.context

            val sharedPref = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val role = sharedPref.getString("role", "")

            if (!role.equals("Teacher")) {
                cbAttendance.visibility = View.GONE
            }

            println("$selectedDate|$selectedTime")

            val keySchedule = schedule.studentSchedule.keys.find { key ->
                schedule.studentSchedule[key] == "$selectedDate|$selectedTime"
            }

            println(keySchedule)

            val studentWeekText = "Week $keySchedule"
            val studentAttendance = schedule.studentAttendance

            tvStudentName.text = schedule.studentName
            tvStudentLevel.text = schedule.levelId
            tvStudentWeek.text = studentWeekText
            tvStudentMaterial.text = schedule.studentAttendanceMaterials[keySchedule] ?: "No material"

            cbAttendance.setOnClickListener {
                if (cbAttendance.isChecked) {
                    check = true
                    db.collection("student")
                        .document(schedule.studentId)
                        .update("studentAttendance", keySchedule!!.toInt())
                        .addOnSuccessListener {
                            Log.d("NearestScheduleAdapter", "Attendance updated successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("NearestScheduleAdapter", "Error updating attendance", e)
                        }
                }
                else if (!cbAttendance.isChecked) {
                    check = false
                    val newAttendanceCount = keySchedule!!.toInt() - 1
                    println("attendance: " + schedule.studentAttendance)
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
                println(!cbAttendance.isChecked)
                println(check)
            }

            println("$keySchedule >= $studentAttendance")

            if (studentAttendance!! >= keySchedule!!.toInt()) {
                cbAttendance.isChecked = true
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