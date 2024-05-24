package com.example.digitrack.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.adapters.AttendancesAdapter
import com.example.digitrack.adapters.DailyReportAdapter
import com.example.digitrack.data.Students
import com.example.digitrack.databinding.ActivityDailyReportBinding
import com.google.firebase.firestore.FirebaseFirestore

class DailyReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDailyReportBinding
    private lateinit var rvDailyReport: RecyclerView
    private val dailyReportList = mutableListOf<Students>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvDailyReport = binding.rvDailyReport
        rvDailyReport.layoutManager = LinearLayoutManager(this)

        loadDailyReport()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadDailyReport() {
        val db = FirebaseFirestore.getInstance()
        db.collection("student").get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot) {
                val student = document.toObject(Students::class.java)
                if (student != null) {
                    Log.d("AttendanceActivity", student.toString())
                    println("Level ID: ${student.levelId}")
                    println("Student Attendance: ${student.studentAttendance}")
                    println("Student Attendance Materials: ${student.studentAttendanceMaterials}")
                    println("Student Daily Report Link: ${student.studentDailyReportLink}")
                    println("Student ID: ${student.studentId}")
                    println("Student Name: ${student.studentName}")
                    println("Student Schedule: ${student.studentSchedule}")
                    println("User ID: ${student.userId}")
                }
                dailyReportList.add(student)
            }

            rvDailyReport.adapter = DailyReportAdapter(dailyReportList) { position ->
                Toast.makeText(this, "Student clicked at position $position", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to load students: $exception", Toast.LENGTH_SHORT).show()
        }
    }
}