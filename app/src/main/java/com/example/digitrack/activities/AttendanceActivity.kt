package com.example.digitrack.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.adapters.AttendancesAdapter
import com.example.digitrack.adapters.MaterialsAdapter
import com.example.digitrack.data.Materials
import com.example.digitrack.data.Students
import com.example.digitrack.databinding.ActivityAttendanceBinding
import com.google.firebase.firestore.FirebaseFirestore

class AttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceBinding

    private lateinit var rvStudentName: RecyclerView

    private val studentList = mutableListOf<Students>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvStudentName = binding.rvAttendance

        rvStudentName.layoutManager = LinearLayoutManager(this)

        loadStudentsName()

        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, AddNewStudentActivity::class.java))
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadStudentsName() {
        val db = FirebaseFirestore.getInstance()
        db.collection("student").get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot) {
                val student = document.toObject(Students::class.java)
                studentList.add(student)
            }
            rvStudentName.adapter = AttendancesAdapter(studentList) { position ->
                // Handle click event for materials
                Toast.makeText(this, "Material clicked at position $position", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to load materials: $exception", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadStudentsData() {
        val db = FirebaseFirestore.getInstance()
        val studentsCollection = db.collection("student")

        studentsCollection
            .get()
            .addOnSuccessListener { querySnapshot ->
                val studentsList = mutableListOf<Students>()
                for (document in querySnapshot) {
                    val student = document.toObject(Students::class.java)
                    studentsList.add(student)
                }
                // Lakukan sesuatu dengan daftar siswa yang kamu dapatkan
                // Misalnya, set adapter RecyclerView
                Toast.makeText(this, studentsList.toString(), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                // Tangani kegagalan saat mengambil data
                Log.w("AttendanceActivity", "Error getting documents: ", exception)
            }
    }

}