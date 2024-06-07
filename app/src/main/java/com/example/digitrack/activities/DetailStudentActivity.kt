package com.example.digitrack.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.digitrack.databinding.ActivityDetailStudentBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DetailStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStudentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val studentName = intent.getStringExtra("studentName")
        val studentLevel = intent.getStringExtra("studentLevel")
        val studentAge = intent.getStringExtra("studentAge")
        val studentDayTime = intent.getStringExtra("studentDayTime")
        val studentAttendance = intent.getStringExtra("studentAttendance")
        val studentTeacher = intent.getStringExtra("studentTeacher")
        val studentDailyReport = intent.getStringExtra("studentDailyReport")
        val studentJoinDate = intent.getStringExtra("studentJoinDate")
        val studentLevelUp = studentJoinDate?.let { LevelUpDate(it) }

        binding.tvHeader.text = studentName
        binding.tvStudentLevel.text = studentLevel
        binding.tvStudentAge.text = studentAge
        binding.tvDateTime.text = studentDayTime
        binding.tvAttendance.text = studentAttendance
        binding.tvTeacher.text = studentTeacher
        binding.tvDailyReport.text = studentDailyReport
        binding.tvJoinDate.text = studentJoinDate
        binding.tvLevelUpDate.text = studentLevelUp

        binding.btnEditProfile.setOnClickListener {
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT)
//            startActivity(Intent(this, EditDetailStudentActivity::class.java))
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    fun LevelUpDate(date: String): String {
        // Format tanggal
        val dateFormatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("dd/MM/yy")
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        // Tanggal input
        val inputDate = date

        // Parse input date
        val date = LocalDate.parse(inputDate, dateFormatter)

        // Tambah 6 bulan
        val newDate = date.plusMonths(6)

        // Format hasil
        val formattedNewDate = newDate.format(dateFormatter)

        // Cetak hasil
        return formattedNewDate
    }
}