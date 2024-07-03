package com.example.digitrack.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.adapters.AttendancesAdapter
import com.example.digitrack.data.Students
import com.example.digitrack.databinding.ActivityAttendanceBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class AttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceBinding
    private lateinit var rvStudentName: RecyclerView
    private lateinit var adapter: AttendancesAdapter
    private val studentList = mutableListOf<Students>()
    private var filteredStudentList = mutableListOf<Students>()
    private var currentDate: LocalDate = LocalDate.now()
    private var monthText: String? = ""
    private var monthNumber: Int = currentDate.monthValue
    private var currentYear: Int = currentDate.year

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val role = sharedPref.getString("role", "")

        if (!role.equals("Admin")) {
            binding.btnAdd.visibility = View.GONE
        }

        rvStudentName = binding.rvAttendance
        rvStudentName.layoutManager = LinearLayoutManager(this)

        adapter = AttendancesAdapter(filteredStudentList, monthNumber, currentYear) {}
        rvStudentName.adapter = adapter

        loadStudentsName()

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

        binding.svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return false
            }
        })
    }

    private fun loadStudentsName() {
        val db = FirebaseFirestore.getInstance()
        db.collection("student").addSnapshotListener { querySnapshot, exception ->
            if (exception != null) {
                Toast.makeText(this, "Failed to load students: $exception", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (querySnapshot != null) {
                studentList.clear()
                for (document in querySnapshot) {
                    val student = document.toObject(Students::class.java)
                    studentList.add(student)
                }
                filter("")
            }
        }
    }

    private fun filter(text: String?) {
        val query = text?.lowercase(Locale.getDefault()) ?: ""
        filteredStudentList.clear()

        if (query.isEmpty()) {
            filteredStudentList.addAll(studentList)
        } else {
            for (student in studentList) {
                val studentName = student.studentName.lowercase(Locale.getDefault())
                val studentLevel = student.levelId.lowercase(Locale.getDefault())
                if (studentName.contains(query) || studentLevel.contains(query)) {
                    filteredStudentList.add(student)
                }
            }
        }
        refreshAdapterData()
    }

    private fun refreshAdapterData() {
        adapter.updateData(filteredStudentList, monthNumber, currentYear)  // Update adapter data without re-fetching students
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

        val monthTvText = "$monthText, $currentYear"
        binding.tvMonth.text = monthTvText
    }
}