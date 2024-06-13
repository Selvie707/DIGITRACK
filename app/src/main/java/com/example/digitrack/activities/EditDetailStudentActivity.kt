package com.example.digitrack.activities

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.digitrack.databinding.ActivityEditDetailStudentBinding
import com.google.firebase.firestore.FirebaseFirestore

class EditDetailStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditDetailStudentBinding
    private val db = FirebaseFirestore.getInstance()
    val levelNames = mutableListOf<String>()
    val teacherNames = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDetailStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val usersCollection = db.collection("student")

        // Fetch data from Firestore
        db.collection("levels")
            .whereEqualTo("curName", "DK3")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val levelName = document.getString("levelName")
                    if (levelName != null) {
                        levelNames.add(levelName)
                    }
                }
                // Set the adapter to Spinner
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, levelNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spLevelEdit.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load levels: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        db.collection("teacher")
            .whereEqualTo("userRole", "Teacher")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val teacherName = document.getString("userName")
                    if (teacherName != null) {
                        teacherNames.add(teacherName)
                    }
                }
                val adapterTeacher = ArrayAdapter(this, android.R.layout.simple_spinner_item, teacherNames)
                adapterTeacher.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spTeacherEdit.adapter = adapterTeacher
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load levels: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        // Mengatur adapter untuk spinner
        val optionTimes = arrayOf("10.00 WIB", "11.00 WIB", "12.00 WIB", "13.00 WIB", "14.00 WIB", "15.00 WIB", "16.00 WIB", "17.00 WIB")
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, optionTimes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spTimeEdit.adapter = adapter

        val studentId = intent.getStringExtra("studentId")
        var studentName = intent.getStringExtra("studentName")
        var studentLevel = intent.getStringExtra("studentLevel")
        val studentAge = intent.getStringExtra("studentAge")
        val studentDayTime = intent.getStringExtra("studentDayTime")
        val studentAttendance = intent.getStringExtra("studentAttendance")
        var studentTeacher = intent.getStringExtra("studentTeacher")
        var studentDailyReport = intent.getStringExtra("studentDailyReport")
        val studentJoinDate = intent.getStringExtra("studentJoinDate")

        // Memisahkan data menjadi tanggal dan waktu
        val separatedData = studentDayTime!!.split("|")

        // Mendapatkan tanggal dan waktu terpisah
        var day = separatedData[0]
        var time = separatedData[1]

        binding.etNameEdit.setText(studentName)
        binding.etDayEdit.setText(day)
        binding.etDailyReportEdit.setText(studentDailyReport)
        binding.spLevelEdit.setSelection(levelNames.indexOf(studentLevel))
        binding.spTimeEdit.setSelection(optionTimes.indexOf(time))
        binding.spTeacherEdit.setSelection(teacherNames.indexOf(studentTeacher))

        binding.btnSaveEditStudent.setOnClickListener {
            studentName = binding.etNameEdit.text.toString()
            day = binding.etDayEdit.text.toString()
            studentDailyReport = binding.etDailyReportEdit.text.toString()
            studentLevel = binding.spLevelEdit.selectedItem.toString().trim()
            time = binding.spTimeEdit.selectedItem.toString().trim()
            studentTeacher = binding.spTeacherEdit.selectedItem.toString().trim()

            val updates = hashMapOf(
                "studentName" to studentName,
                "levelId" to studentLevel,
                "studentDayTime" to "$day|$time WIB",
                "userId" to studentTeacher,
                "studentDailyReport" to studentDailyReport
            )

            // Perbarui nilai di Firestore
            db.collection("student").document(studentId!!).update(updates as Map<String, Any>)
                .addOnSuccessListener {
                    // Penanganan sukses
                    println("Nilai berhasil diperbarui!")
                }
                .addOnFailureListener { e ->
                    // Penanganan kesalahan
                    println("Gagal memperbarui nilai: $e")
                }

            finish()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}