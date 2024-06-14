package com.example.digitrack.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.adapters.OuterScheduleAdapter
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
                val filteredSchedules = mutableListOf<Pair<Students, String>>()
                for ((student, schedule) in studentsWithSchedule) {
                    for ((_, value) in schedule) {
                        val scheduleDate = value.split("|").getOrNull(0) ?: ""
                        if (scheduleDate == date) {
                            filteredSchedules.add(Pair(student, value))
                        }
                    }
                }

                // Group schedules by time
                val groupedByTime = filteredSchedules.groupBy { (_, scheduleKey) ->
                    scheduleKey.split("|")[1]
                }

                // Set adapter dengan daftar yang sudah difilter
                rvSchedule.adapter = OuterScheduleAdapter(groupedByTime, date)
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