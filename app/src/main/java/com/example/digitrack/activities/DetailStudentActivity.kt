package com.example.digitrack.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.digitrack.databinding.ActivityDetailStudentBinding
import com.google.firebase.firestore.FirebaseFirestore

class DetailStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStudentBinding

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_STUDENT_REQUEST_CODE && resultCode == RESULT_OK) {
            val studentId = data?.getStringExtra("studentId")
            studentId?.let {
                fetchStudentData(it)
            }
        }
    }

    private fun fetchStudentData(studentId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("student").document(studentId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val studentName = document.getString("studentName")
                    val studentLevel = document.getString("levelId")
                    val studentAge = document.getString("studentAge")
                    val studentDayTime = document.getString("studentDayTime")
                    val studentAttendance = document.getLong("studentAttendance")?.toString()
                    val studentTeacher = document.getString("teacherName")
                    val studentDailyReport = document.getString("studentDailyReport")
                    val studentJoinDate = document.getString("studentJoinDate")
                    val studentLevelUp = document.getString("studentLevelUp")

                    binding.tvHeader.text = studentName
                    binding.tvStudentLevel.text = studentLevel
                    binding.tvStudentAge.text = studentAge
                    binding.tvDateTime.text = studentDayTime
                    binding.tvAttendance.text = studentAttendance
                    binding.tvTeacher.text = studentTeacher
                    binding.tvDailyReport.text = studentDailyReport
                    binding.tvJoinDate.text = studentJoinDate
                    binding.tvLevelUpDate.text = studentLevelUp
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load student data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        const val EDIT_STUDENT_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val role = sharedPref.getString("role", "")

        if (!role.equals("Admin")) {
            binding.btnEditProfile.visibility = View.GONE
            binding.btnDelete.visibility = View.GONE
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
        val studentLevelUp = intent.getStringExtra("studentLevelUp")

        binding.tvHeader.text = studentName
        binding.tvStudentLevel.text = studentLevel
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
            startActivityForResult(intent, EDIT_STUDENT_REQUEST_CODE)
        }

        binding.btnDelete.setOnClickListener {
            deleteDocumentById(studentId!!)
            finish()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun deleteDocumentById(studentId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("student").document(studentId)
            .delete()
            .addOnSuccessListener {
                println("Document successfully deleted!")
            }
            .addOnFailureListener { e ->
                println("Error deleting document: $e")
            }
    }
}