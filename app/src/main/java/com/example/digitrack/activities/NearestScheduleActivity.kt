package com.example.digitrack.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.BuildConfig
import com.example.digitrack.R
import com.example.digitrack.adapters.NearestScheduleAdapter
import com.example.digitrack.data.Students
import com.example.digitrack.databinding.ActivityNearestScheduleBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class NearestScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNearestScheduleBinding
    private lateinit var rvSchedule: RecyclerView

    companion object {
        const val REQUEST_CODE_NOTIFICATIONS = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNearestScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = FirebaseFirestore.getInstance()

        db.collection("AppVersion")
            .orderBy("avId", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener {
                // TODO
                for (document in it) {
                    val avId = document.getLong("avId")
                    val source = document.get("avSource")
                    println("avID: $avId")

                    val x = BuildConfig.VERSION_CODE

                    println(x)
                    if (x < avId!!) {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(source.toString())
                        startActivity(intent)
                    }
                }

                // TODO: Alert dialog deskripsi fitur

            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load students: $exception", Toast.LENGTH_SHORT).show()
            }

        // TODO: Bikin di dalam else semua kode jika versi app normal

        // Meminta izin notifikasi pada Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_NOTIFICATIONS
                )
            }
        }

        val user = Firebase.auth.currentUser
        if (user != null) {
            println("Login Successfully")
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

        // Mendapatkan tanggal hari ini dalam format yang diminta
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yy"))

        // Mendapatkan jam saat ini dan mengubahnya menjadi jam awal rentang jam sekarang
        val currentHour = LocalTime.now().hour.toString().padStart(2, '0')
        val nextHour = (LocalTime.now().hour + 1).toString().padStart(2, '0')
        val currentMinute = "00"
        val currentTime = "$currentHour.$currentMinute"
        val nextTime = "$nextHour.$currentMinute"

        val timeText = "JAM $currentTime - $nextTime WIB"
        binding.tvJam.text = timeText

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

                // Filter jadwal berdasarkan tanggal dan jam sekarang
                val filteredSchedules = studentsWithSchedule.flatMap { (student, schedule) ->
                    schedule.filter { (_, value) ->
                        val scheduleDate = value.split("|").getOrNull(0) ?: ""
                        val scheduleTime = value.split("|").getOrNull(1) ?: ""
                        scheduleDate == currentDate && scheduleTime.startsWith(currentHour)
                    }.map { entry -> Triple(student, entry.key, entry.value) }
                }

                // Ekstraksi dan konversi waktu dari kunci
                val studentsWithTimes = filteredSchedules.map { (student, scheduleKey, _) ->
                    val timeString = scheduleKey.split("|").getOrNull(1) ?: "00.00"
                    val timeParts = timeString.split(".")
                    val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 0
                    val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
                    val totalMinutes = hour * 60 + minute
                    Triple(student, scheduleKey, totalMinutes)
                }

                // Mengurutkan berdasarkan waktu
                val sortedStudents = studentsWithTimes.sortedBy { it.third }

                // Set adapter dengan daftar yang sudah diurutkan
                rvSchedule.adapter = NearestScheduleAdapter(sortedStudents.map { it.first }, currentDate, currentTime) { position ->
                    Toast.makeText(this, "Student clicked at position $position", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load students: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    // Firebase Storage
    private fun getApkDownloadUrl(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("apk/your_apk_file_name.apk")

        storageRef.downloadUrl.addOnSuccessListener { uri ->
            onSuccess(uri.toString())
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }
}