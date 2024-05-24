package com.example.digitrack.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.R
import com.example.digitrack.adapters.ScheduleAdapter
import com.example.digitrack.data.Students
import com.example.digitrack.databinding.ActivityScheduleBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class ScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScheduleBinding
    private lateinit var rvSchedule: RecyclerView
    private var currentDate: LocalDate = LocalDate.now()
    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yy")
    private val scheduleList = mutableListOf<Students>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvSchedule = binding.rvSchedule
        rvSchedule.layoutManager = LinearLayoutManager(this)

        val hari = getDayOfWeekText(currentDate)
        binding.tvDate.text = currentDate.format(dateFormatter)
        binding.tvDay.text = hari

        loadSchedule(currentDate.format(dateFormatter))

        binding.btnPrevDay.setOnClickListener {
            previousDate()
            loadSchedule(currentDate.format(dateFormatter))
            binding.tvDate.text = currentDate.format(dateFormatter)
            binding.tvDay.text = getDayOfWeekText(currentDate)
        }

        binding.btnNextDay.setOnClickListener {
            nextDate()
            loadSchedule(currentDate.format(dateFormatter))
            binding.tvDate.text = currentDate.format(dateFormatter)
            binding.tvDay.text = getDayOfWeekText(currentDate)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadSchedule(date: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("student")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val studentsWithSchedule = mutableListOf<Pair<Students, Map<String, String>>>()

                for (document in querySnapshot) {
                    val student = document.toObject(Students::class.java)
                    val studentSchedule = document.get("studentSchedule") as? Map<String, String>
                    if (studentSchedule != null) {
                        studentsWithSchedule.add(Pair(student, studentSchedule))
                    }
                }

                // Filter jadwal berdasarkan tanggal yang diberikan
                val filteredStudents = studentsWithSchedule.filter { (_, schedule) ->
                    schedule.any { (key, _) ->
                        val scheduleDate = key.split("|").getOrNull(0) ?: ""
                        scheduleDate == date
                    }
                }

                // Logging hasil filter
                filteredStudents.forEach { (student, schedule) ->
                    schedule.forEach { (key, _) ->
                        val scheduleDate = key.split("|").getOrNull(0) ?: ""
                        if (scheduleDate == date) {
                            Log.d("FilteredStudent", "Student: ${student.studentName}, Date: $scheduleDate")
                        }
                    }
                }

                // Set adapter dengan daftar yang sudah difilter
                rvSchedule.adapter = ScheduleAdapter(filteredStudents.map { it.first }) { position ->
                    Toast.makeText(this, "Student clicked at position $position", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load students: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getDayOfWeekText(date: LocalDate): String {
        val dayOfWeek = date.dayOfWeek
        return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }

    private fun previousDate() {
        currentDate = currentDate.minusDays(1)
    }

    private fun nextDate() {
        currentDate = currentDate.plusDays(1)
    }
}