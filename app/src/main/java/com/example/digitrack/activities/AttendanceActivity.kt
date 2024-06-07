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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class AttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceBinding
    private lateinit var rvStudentName: RecyclerView
    private lateinit var adapter: AttendancesAdapter
    private val studentList = mutableListOf<Students>()
    private var currentDate: LocalDate = LocalDate.now()
    private var monthText: String? = ""
    private var monthNumber: Int = currentDate.monthValue
    private var currentYear: Int = currentDate.year
    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvStudentName = binding.rvAttendance
        rvStudentName.layoutManager = LinearLayoutManager(this)

        adapter = AttendancesAdapter(studentList, monthNumber, currentYear) { position ->
            Toast.makeText(this, "Student clicked at position $position", Toast.LENGTH_SHORT).show()
        }
        rvStudentName.adapter = adapter

        // Load student data initially
        loadStudentsName()

        // Set the current month and year text
        updateMonthYearText()

        binding.btnPrevMonth.setOnClickListener {
            previousDate()
            updateMonthYearText()
            refreshAdapterData()
        }

        binding.btnNextMonth.setOnClickListener {
            nextDate()
            updateMonthYearText()
            refreshAdapterData()
        }

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
            studentList.clear() // Clear the list before adding new data
            for (document in querySnapshot) {
                val student = document.toObject(Students::class.java)
                if (student != null) {
                    studentList.add(student)
                }
            }
            Log.d("MonthNumber", monthNumber.toString())
            adapter.updateData(studentList, monthNumber, currentYear)
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to load students: $exception", Toast.LENGTH_SHORT).show()
        }
    }

    private fun refreshAdapterData() {
        adapter.updateData(studentList, monthNumber, currentYear)  // Update adapter data without re-fetching students
    }

    private fun previousDate() {
        currentDate = currentDate.minusMonths(1)
        monthNumber = currentDate.monthValue
        currentYear = currentDate.year
    }

    private fun nextDate() {
        currentDate = currentDate.plusMonths(1)
        monthNumber = currentDate.monthValue
        currentYear = currentDate.year
    }

    private fun updateMonthYearText() {
        monthText = currentDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        binding.tvMonth.text = "$monthText, $currentYear"
    }
}