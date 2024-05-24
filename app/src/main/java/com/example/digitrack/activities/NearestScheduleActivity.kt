package com.example.digitrack.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.R
import com.example.digitrack.adapters.NearestScheduleAdapter
import com.example.digitrack.adapters.ScheduleAdapter
import com.example.digitrack.data.Students
import com.example.digitrack.databinding.ActivityNearestScheduleBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class NearestScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNearestScheduleBinding
    private lateinit var rvSchedule: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNearestScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = Firebase.auth.currentUser
        if (user != null) {
            Toast.makeText(this, "Login Berhasil!!!", Toast.LENGTH_SHORT).show()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        rvSchedule = binding.rvNearestSchedule
        rvSchedule.layoutManager = LinearLayoutManager(this)

        loadNearestSchedule()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.btn_session
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btn_session -> true
                R.id.btn_home -> {
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                    overridePendingTransition(0,0)
                    finish()
                    true
                }
                R.id.btn_profile -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    overridePendingTransition(0,0)
                    finish()
                    true
                }
                else -> false
            }
        }

    }

    private fun loadNearestSchedule() {
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
                rvSchedule.adapter = NearestScheduleAdapter(sortedStudents.map { it.first }) { position ->
                    Toast.makeText(this, "Student clicked at position $position", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load students: $exception", Toast.LENGTH_SHORT).show()
            }
    }
}