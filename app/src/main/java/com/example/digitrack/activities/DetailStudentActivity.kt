package com.example.digitrack.activities

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.digitrack.databinding.ActivityDetailStudentBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DetailStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStudentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val role = sharedPref.getString("role", "")

        if (!role.equals("Admin")) {
            binding.btnEditProfile.visibility = View.GONE
        }

        val studentId = intent.getStringExtra("studentId")
        val studentName = intent.getStringExtra("studentName")
        val studentLevel = intent.getStringExtra("studentLevel")
        val studentAge = intent.getStringExtra("studentAge")
        val studentDayTime = intent.getStringExtra("studentDayTime")
        val studentAttendance = intent.getStringExtra("studentAttendance")
        val studentTeacher = intent.getStringExtra("studentTeacher")
        val studentDailyReport = intent.getStringExtra("studentDailyReport")
        val studentJoinDate = intent.getStringExtra("studentJoinDate")
        val studentLevelUp = studentJoinDate?.let { LevelUpDate(it) }

        println("Level: " + studentLevelUp(studentLevelUp!!, studentId!!, studentLevel!!))

        binding.tvHeader.text = studentName
        binding.tvStudentLevel.text = studentLevelUp(studentLevelUp!!, studentId!!, studentLevel!!)
        binding.tvStudentAge.text = studentAge
        binding.tvDateTime.text = studentDayTime
        binding.tvAttendance.text = studentAttendance
        binding.tvTeacher.text = studentTeacher
        binding.tvDailyReport.text = studentDailyReport
        binding.tvJoinDate.text = studentJoinDate
        binding.tvLevelUpDate.text = studentLevelUp

        val level = binding.tvStudentLevel.text.toString()

        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditDetailStudentActivity::class.java).apply {
                putExtra("studentId", studentId)
                putExtra("studentName", studentName)
                putExtra("studentLevel", level)
                putExtra("studentAge", studentAge)
                putExtra("studentDayTime", studentDayTime)
                putExtra("studentAttendance", studentAttendance)
                putExtra("studentTeacher", studentTeacher)
                putExtra("studentDailyReport", studentDailyReport)
                putExtra("studentJoinDate", studentJoinDate)
            }
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    fun LevelUpDate(date: String): String {
        // Format tanggal
        val dateFormatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("dd-MM-yy")
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

    fun studentLevelUp(levelUpDate: String, studentId: String, currentLevel: String): String {
        // Ubah format tanggal yang diinput ke LocalDate
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yy")
        val parsedInputDate = LocalDate.parse(levelUpDate, formatter)
        var nextLevel = ""

        // Tanggal hari ini
        val currentDate = LocalDate.now()

        // Periksa apakah tanggal yang diinput lebih kecil atau sama dengan tanggal hari ini
        if (parsedInputDate.isBefore(currentDate) || parsedInputDate.isEqual(currentDate)) {
            println("Tanggal yang diinput lebih kecil atau sama dengan tanggal hari ini.")
            // Lakukan tindakan yang sesuai di sini

            val db = FirebaseFirestore.getInstance()

            nextLevel = when (currentLevel) {
                "LC1L1" -> "LC1L2"
                "J1" -> "J2"
                "J2" -> "T1"
                // Tambahkan kasus lain jika diperlukan
                else -> {
                    // Tambahkan tindakan yang sesuai jika level tidak cocok dengan kasus yang ada
                    println("Level tidak dikenali.")
                    ""
                }
            }

            val updates = hashMapOf(
                "levelId" to nextLevel,
                "studentJoinDate" to levelUpDate
            )

            println("Next level: $nextLevel")

            // Perbarui nilai di Firestore
            db.collection("student").document(studentId).update(updates as Map<String, Any>)
                .addOnSuccessListener {
                    // Penanganan sukses
                    println("Nilai berhasil diperbarui!")
                }
                .addOnFailureListener { e ->
                    // Penanganan kesalahan
                    println("Gagal memperbarui nilai: $e")
                }
        } else {
            nextLevel = currentLevel
            println("Tanggal yang diinput lebih besar dari tanggal hari ini.")
            // Lakukan tindakan yang sesuai di sini
        }

        return nextLevel
    }
}