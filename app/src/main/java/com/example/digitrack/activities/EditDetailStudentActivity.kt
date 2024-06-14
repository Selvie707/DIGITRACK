package com.example.digitrack.activities

import android.R
import android.app.DatePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.digitrack.databinding.ActivityEditDetailStudentBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class EditDetailStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditDetailStudentBinding
    private val db = FirebaseFirestore.getInstance()
    private val levelNames = mutableListOf<String>()
    private val teacherNames = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDetailStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menangani pemilihan tanggal ketika TextView diklik
        binding.etDayEdit.setOnClickListener {
            showDatePickerDialog(this) { selectedDate ->
                binding.etDayEdit.text = selectedDate
            }
        }

        // Menangani pemilihan tanggal ketika TextView diklik
        binding.etJoinDate.setOnClickListener {
            showDatePickerDialog(this) { selectedDate ->
                binding.etJoinDate.text = selectedDate
            }
        }

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
                val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, levelNames)
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
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
                val adapterTeacher = ArrayAdapter(this, R.layout.simple_spinner_item, teacherNames)
                adapterTeacher.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                binding.spTeacherEdit.adapter = adapterTeacher
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load levels: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        // Mengatur adapter untuk spinner
        val optionTimes = arrayOf("10.00 WIB", "11.00 WIB", "12.00 WIB", "13.00 WIB", "14.00 WIB", "15.00 WIB", "16.00 WIB", "17.00 WIB")
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, optionTimes)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spTimeEdit.adapter = adapter

        val studentId = intent.getStringExtra("studentId")
        var studentName = intent.getStringExtra("studentName")
        var studentLevel = intent.getStringExtra("studentLevel")
        var studentAge = intent.getStringExtra("studentAge")
        val studentDayTime = intent.getStringExtra("studentDayTime")
        var studentAttendance = intent.getStringExtra("studentAttendance")
        var studentTeacher = intent.getStringExtra("studentTeacher")
        var studentDailyReport = intent.getStringExtra("studentDailyReport")
        var studentJoinDate = intent.getStringExtra("studentJoinDate")

        // Memisahkan data menjadi tanggal dan waktu
        val separatedData = studentDayTime!!.split("|")

        // Mendapatkan tanggal dan waktu terpisah
        var day = separatedData[0]
        var time = separatedData[1]

        binding.etNameEdit.setText(studentName)
        binding.etDayEdit.text = day
        binding.etAge.setText(studentAge)
        binding.etAttendance.setText(studentAttendance)
        binding.etJoinDate.text = studentJoinDate
        binding.etDailyReportEdit.setText(studentDailyReport)
        binding.spLevelEdit.setSelection(levelNames.indexOf(studentLevel))
        binding.spTimeEdit.setSelection(optionTimes.indexOf(time))
        binding.spTeacherEdit.setSelection(teacherNames.indexOf(studentTeacher))

        binding.btnSaveEditStudent.setOnClickListener {
            studentName = binding.etNameEdit.text.toString()
            day = binding.etDayEdit.text.toString()
            studentAge = binding.etAge.text.toString()
            studentAttendance = binding.etAttendance.text.toString()
            studentJoinDate = binding.etJoinDate.text.toString()
            studentDailyReport = binding.etDailyReportEdit.text.toString()
            studentLevel = binding.spLevelEdit.selectedItem.toString().trim()
            time = binding.spTimeEdit.selectedItem.toString().trim()
            studentTeacher = binding.spTeacherEdit.selectedItem.toString().trim()

            val updates = hashMapOf(
                "studentName" to studentName,
                "levelId" to studentLevel,
                "studentAge" to studentAge,
                "studentAttendance" to studentAttendance,
                "studentJoinDate" to studentJoinDate,
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

    private fun showDatePickerDialog(context: Context, onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
            // Mengonversi tahun ke format dua digit (contoh: 2024 -> 24)
            val formattedYear = (selectedYear % 100).toString().padStart(2, '0')

            // Format tanggal menjadi "dd-MM-yy"
            val formattedDate = "${selectedDay.toString().padStart(2, '0')}-${(selectedMonth + 1).toString().padStart(2, '0')}-$formattedYear"
            onDateSelected(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }
}