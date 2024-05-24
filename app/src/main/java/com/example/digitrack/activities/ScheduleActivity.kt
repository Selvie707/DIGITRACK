package com.example.digitrack.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.R
import com.example.digitrack.adapters.AttendancesAdapter
import com.example.digitrack.adapters.ScheduleAdapter
import com.example.digitrack.data.Students
import com.example.digitrack.databinding.ActivityScheduleBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class ScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScheduleBinding
    private lateinit var rvSchedule: RecyclerView
    private var currentDate: LocalDate = LocalDate.now()
    private val scheduleList = mutableListOf<Students>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvSchedule = binding.rvSchedule
        rvSchedule.layoutManager = LinearLayoutManager(this)

        loadSchedule()

        val today: LocalDate = getCurrentDate()
        println("Hari ini: $today")

        val yesterday: LocalDate = LocalDate.now().minusDays(1)
        val tomorrow: LocalDate = LocalDate.now().plusDays(1)

        println("Kemarin: $yesterday")
        println("Besok: $tomorrow")

        if (isWeekday(today)) {
            println("$today adalah hari kerja (Senin - Sabtu) dalam seminggu.")
        } else {
            println("$today adalah hari libur (Minggu) dalam seminggu.")
        }

        val hari = getDayOfWeekText(currentDate)

        binding.tvDate.text = currentDate.toString()
        binding.tvDay.text = hari

        binding.btnPrevDay.setOnClickListener {
            previousDate()
            binding.tvDate.text = currentDate.toString()
            binding.tvDay.text = getDayOfWeekText(currentDate)
        }

        binding.btnNextDay.setOnClickListener {
            nextDate()
            binding.tvDate.text = currentDate.toString()
            binding.tvDay.text = getDayOfWeekText(currentDate)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadSchedule() {
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

                // Mengambil entri pertama dari setiap studentSchedule dan mengurutkannya berdasarkan waktu
                val firstEntries = studentsWithSchedule.mapNotNull { (student, schedule) ->
                    schedule.toSortedMap().entries.firstOrNull()?.let { entry ->
                        Pair(student, entry)
                    }
                }

                // Ekstraksi dan konversi waktu dari kunci
                val studentsWithFirstTime = firstEntries.map { (student, entry) ->
                    val timeString = entry.key.split("|").getOrNull(1) ?: "00.00"
                    val timeParts = timeString.split(".")
                    val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 0
                    val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
                    val totalMinutes = hour * 60 + minute
                    Triple(student, entry.key, totalMinutes)
                }

                // Mengurutkan berdasarkan waktu
                val sortedStudents = studentsWithFirstTime.sortedBy { it.third }

                // Logging hasil urutan
                sortedStudents.forEach { (student, scheduleKey, _) ->
                    Log.d("SortedStudent", "Student: ${student.studentName}, Time: ${scheduleKey.split("|").getOrNull(1)}")
                }

                // Set adapter dengan daftar yang sudah diurutkan
                rvSchedule.adapter = ScheduleAdapter(sortedStudents.map { it.first }) { position ->
                    Toast.makeText(this, "Student clicked at position $position", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load students: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    fun isWeekday(date: LocalDate): Boolean {
        val dayOfWeek = date.dayOfWeek
        return dayOfWeek in DayOfWeek.MONDAY..DayOfWeek.SATURDAY
    }

    fun getDayOfWeekText(date: LocalDate): String {
        val dayOfWeek = date.dayOfWeek
        return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }

    fun getCurrentDate(): LocalDate {
        return currentDate
    }

    fun previousDate() {
        if (currentDate.dayOfWeek == DayOfWeek.MONDAY) {
            Toast.makeText(this, "Can't access", Toast.LENGTH_SHORT).show()
        } else {
            currentDate = currentDate.minusDays(1)
        }
    }

    fun nextDate() {
        if (currentDate.dayOfWeek == DayOfWeek.SATURDAY) {
            Toast.makeText(this, "Can't access", Toast.LENGTH_SHORT).show()
        } else {
            currentDate = currentDate.plusDays(1)
        }
    }
}