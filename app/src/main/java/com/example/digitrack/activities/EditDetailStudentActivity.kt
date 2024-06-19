package com.example.digitrack.activities

import android.R
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.digitrack.databinding.ActivityEditDetailStudentBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
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

        val studentId = intent.getStringExtra("studentId")
        var studentName = intent.getStringExtra("studentName")
        var studentLevel = intent.getStringExtra("studentLevel")
        var studentAge = intent.getStringExtra("studentAge")
        val studentDayTime = intent.getStringExtra("studentDayTime")
        var studentAttendance = intent.getStringExtra("studentAttendance")
        var studentTeacher = intent.getStringExtra("studentTeacher")
        var studentDailyReport = intent.getStringExtra("studentDailyReportLink")
        var studentJoinDate = intent.getStringExtra("studentJoinDate")

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
                    val levelName = document.getString("levelId")
                    if (levelName != null) {
                        levelNames.add(levelName)
                    }
                }
                // Set the adapter to Spinner
                val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, levelNames)
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                binding.spLevelEdit.adapter = adapter

                binding.spLevelEdit.setSelection(levelNames.indexOf(studentLevel))
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load levels: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        // Mengatur OnItemSelectedListener untuk Spinner
        binding.spLevelEdit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString().substring(0,3)
                if (selectedItem == "DK3") {
                    // Menyembunyikan EditText jika item yang dipilih adalah "DK3"
                    binding.etDailyReportEdit.visibility = View.GONE
                } else {
                    // Menampilkan EditText untuk item lainnya
                    binding.etDailyReportEdit.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Tidak melakukan apa-apa
            }
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

                binding.spTeacherEdit.setSelection(teacherNames.indexOf(studentTeacher))
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load levels: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        // Mengatur adapter untuk spinner
        val optionTimes = arrayOf("10.00 WIB", "11.00 WIB", "12.00 WIB", "13.00 WIB", "14.00 WIB", "15.00 WIB", "16.00 WIB", "17.00 WIB")
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, optionTimes)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spTimeEdit.adapter = adapter

        // Mengatur adapter untuk spinner
        val optionDays = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val adapterDay = ArrayAdapter(this, R.layout.simple_spinner_item, optionDays)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spDayEdit.adapter = adapterDay

        // Memisahkan data menjadi tanggal dan waktu
        val separatedData = studentDayTime!!.split("|")

        // Mendapatkan tanggal dan waktu terpisah
        var day = separatedData[0]
        var time = separatedData[1]

        println(day)

        binding.tvJudulStudent.text = studentName
        binding.etNameEdit.setText(studentName)
        binding.etAge.setText(studentAge)
        binding.etAttendance.setText(studentAttendance)
        binding.etJoinDate.text = studentJoinDate
        binding.etDailyReportEdit.setText(studentDailyReport)
        binding.spDayEdit.setSelection(optionDays.indexOf(day))
        binding.spTimeEdit.setSelection(optionTimes.indexOf(time))

        binding.btnSaveEditStudent.setOnClickListener {
            studentName = binding.etNameEdit.text.toString()
            studentAge = binding.etAge.text.toString()
            studentAttendance = binding.etAttendance.text.toString()
            studentJoinDate = binding.etJoinDate.text.toString()
            studentDailyReport = binding.etDailyReportEdit.text.toString().trim()
            studentLevel = binding.spLevelEdit.selectedItem.toString().trim()
            day = binding.spDayEdit.selectedItem.toString().trim()
            time = binding.spTimeEdit.selectedItem.toString().trim()
            studentTeacher = binding.spTeacherEdit.selectedItem.toString().trim()

            if (studentDailyReport!!.isEmpty()) {
                studentDailyReport = "No daily report"
            }

            getStudentSchedule(studentId!!, studentAttendance!!.toInt(), day,
                time.split(" ")[0], studentJoinDate!!
            )

            val updates = hashMapOf(
                "studentName" to studentName,
                "levelId" to studentLevel,
                "studentAge" to studentAge,
                "studentAttendance" to studentAttendance!!.toInt(),
                "studentJoinDate" to studentJoinDate,
                "studentDayTime" to "$day|$time",
                "teacherName" to studentTeacher,
                "studentDailyReportLink" to studentDailyReport
            )

            // Perbarui nilai di Firestore
            db.collection("student").document(studentId).update(updates as Map<String, Any>)
                .addOnSuccessListener {
                    // Set result untuk memberitahu bahwa data telah diperbarui
                    val resultIntent = Intent().apply {
                        putExtra("studentId", studentId)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
                .addOnFailureListener { e ->
                    // Penanganan kesalahan
                    println("Gagal memperbarui nilai: $e")
                }
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

    private fun getStudentSchedule(studentId: String, scheduleKey: Int, schDay: String, schTime: String, joinDate: String) {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yy")

        val dayOfWeek = when (schDay) {
            "Sunday" -> DayOfWeek.SUNDAY
            "Monday" -> DayOfWeek.MONDAY
            "Tuesday" -> DayOfWeek.TUESDAY
            "Wednesday" -> DayOfWeek.WEDNESDAY
            "Thursday" -> DayOfWeek.THURSDAY
            "Friday" -> DayOfWeek.FRIDAY
            "Saturday" -> DayOfWeek.SATURDAY
            else -> throw IllegalArgumentException("Invalid day of the week: $schDay")
        }

        val startDate = LocalDate.parse(joinDate, formatter).plusWeeks(scheduleKey.toLong())

        println(startDate)

        val firstSession = if (startDate.dayOfWeek == dayOfWeek) {
            startDate
        } else {
            startDate.with(TemporalAdjusters.next(dayOfWeek))
        }

        println("First Session: $firstSession")

        val scheduleMap = hashMapOf<String, String>()
        var j = 0
        for (i in scheduleKey+1 until 16) {
            val sessionDate = firstSession.plusWeeks(j.toLong())
            val sessionDateString = sessionDate.format(formatter)
            scheduleMap["studentSchedule." + (i).toString()] = "$sessionDateString|$schTime"
            j++
        }

        val db = FirebaseFirestore.getInstance()

        // Perbarui nilai di Firestore
        db.collection("student").document(studentId)
            .update(scheduleMap as Map<String, Any>)
            .addOnSuccessListener {
                // Penanganan sukses
                println("Jadwal berhasil diperbarui!")
            }
            .addOnFailureListener { e ->
                // Penanganan kesalahan
                println("Gagal memperbarui jadwal: $e")
            }

        db.collection("student").document(studentId)
            .update("studentDayTime", "$schDay|$schTime")
            .addOnSuccessListener {
                // Penanganan sukses
                println("Jadwal berhasil diperbarui!")
            }
            .addOnFailureListener { e ->
                // Penanganan kesalahan
                println("Gagal memperbarui jadwal: $e")
            }
    }
}