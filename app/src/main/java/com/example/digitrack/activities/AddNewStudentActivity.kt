package com.example.digitrack.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.digitrack.databinding.ActivityAddNewStudentBinding
import com.google.firebase.firestore.FirebaseFirestore

class AddNewStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewStudentBinding
    private lateinit var name: String
    private lateinit var level: String
    private lateinit var schDay: String
    private lateinit var schTime: String
    private lateinit var joinDate: String
    private lateinit var teacher: String
    private lateinit var age: String
    private lateinit var dayTime: String
    private lateinit var dailyReportLink: String
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val usersCollection = db.collection("student")

        binding.btnAddAddStudent.setOnClickListener {
            name = binding.etNameAdd.text.toString().trim()
//            level = binding.spLevelAdd.selectedItem.toString().trim()
            joinDate = binding.etJoinDate.text.toString().trim()
            schDay = binding.etDay.text.toString().trim()
//            schTime = binding.spTime.selectedItem.toString().trim()
//            teacher = binding.spTeacher.selectedItem.toString().trim()
            dailyReportLink = binding.etDailyReportAdd.text.toString().trim()
            dayTime = "$schDay|12.00"
            val attendance = 0
            age = binding.etAgeAdd.text.toString().trim()

            if (name.isBlank()) {
                binding.etNameAdd.error = "Please fill up this field"
            } else if (joinDate.isBlank()) {
                binding.etJoinDate.error = "Please fill up this field"
            } else if (schDay.isBlank()) {
                binding.etDay.error = "Please fill up this field"
            } else if (dailyReportLink.isBlank()) {
                binding.etDailyReportAdd.error = "Please fill up this field"
            } else {
                val userId = usersCollection.document().id

                val userMap = hashMapOf(
                    "studentId" to userId,
                    "levelId" to "level",
                    "userId" to "teacher",
                    "studentName" to name,
                    "studentAttendance" to attendance,
                    "studentAttendanceMaterials" to hashMapOf(
                        "1" to "m1",
                        "2" to "m2"
                        // tambahkan material lainnya sesuai kebutuhan
                    ),
                    "studentSchedule" to hashMapOf(
                        "05/06/24" to "1",
                        "12/06/24" to "2"
                        // tambahkan jadwal lainnya sesuai kebutuhan
                    ),
                    "studentDailyReportLink" to dailyReportLink,
                    "studentAge" to age,
                    "studentJoinDate" to joinDate,
                    "studentDayTime" to dayTime
                )

                usersCollection.document(userId!!).set(userMap).addOnSuccessListener {
                    Toast.makeText(this, "Successfully Added!!!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "There's something wrong", Toast.LENGTH_SHORT).show()
                        Log.d("RegisterActivity", "Error: ${e.message}")
                    }
            }

            usersCollection
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        // Mendapatkan data dari setiap dokumen
                        val name = document.getString("name")
                        val email = document.getString("email")
                        // Lakukan sesuatu dengan data yang didapatkan
                        Log.d("Firestore", "$name: $email")

                        startActivity(Intent(this, AttendanceActivity::class.java))
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("Firestore", "Error getting documents: ", exception)
                }

//            db.collection("student").document("abc").set(userMap)
//                .addOnSuccessListener {
//
//                }
//                .addOnFailureListener {
//                    Toast.makeText(this, "Failed!!!", Toast.LENGTH_SHORT).show()
//                    Log.d("bbb", "bbb")
//                }
//
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
        }

//        binding.btnAddAddStudent.setOnClickListener {
//            val intent = Intent(this, AttendanceActivity::class.java)
//            intent.putExtra("key", "")
//            startActivity(intent)
//        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}