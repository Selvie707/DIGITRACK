package com.example.digitrack.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.adapters.AttendancesAdapter
import com.example.digitrack.data.Students
import com.example.digitrack.databinding.ActivityAttendanceBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class AttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceBinding
    private lateinit var rvStudentName: RecyclerView
    private val studentList = mutableListOf<Students>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvStudentName = binding.rvAttendance
        rvStudentName.layoutManager = LinearLayoutManager(this)

        loadStudentsName()

        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, AddNewStudentActivity::class.java))
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadStudentsName() {
        val db = FirebaseFirestore.getInstance()
        db.collection("student").get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot) {
                val student = document.toObject(Students::class.java)
                if (student != null) {
                    val studentAttendanceMaterials = document.data?.get("studentAttendanceMaterials") as? Map<String, String>
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
                studentList.add(student)
            }

            rvStudentName.adapter = AttendancesAdapter(studentList) { position ->
                Toast.makeText(this, "Student clicked at position $position", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to load students: $exception", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadStudentsData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("student").get().addOnSuccessListener { querySnapshot ->
            val studentsList = mutableListOf<Students>()
            for (document in querySnapshot) {
                val student = document.toObject(Students::class.java)
                studentsList.add(student)
            }
            // Use the studentsList as needed
            Toast.makeText(this, studentsList.toString(), Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { exception ->
            Log.w("AttendanceActivity", "Error getting documents: ", exception)
        }
    }

    fun getCurrentMonth(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.MONTH) + 1 // Bulan dimulai dari indeks 0, jadi tambahkan 1
    }
}