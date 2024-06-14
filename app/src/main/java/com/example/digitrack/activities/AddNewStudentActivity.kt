package com.example.digitrack.activities

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.digitrack.databinding.ActivityAddNewStudentBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Calendar

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
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val usersCollection = db.collection("student")

        // Fetch data from Firestore
        db.collection("levels")
            .whereEqualTo("curName", "DK3")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val levelNames = mutableListOf<String>()
                for (document in querySnapshot) {
                    val levelName = document.getString("levelName")
                    if (levelName != null) {
                        levelNames.add(levelName)
                    }
                }
                // Set the adapter to Spinner
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, levelNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spLevelAdd.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load levels: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        db.collection("teacher")
            .whereEqualTo("userRole", "Teacher")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val teacherNames = mutableListOf<String>()
                for (document in querySnapshot) {
                    val teacherName = document.getString("userName")
                    if (teacherName != null) {
                        teacherNames.add(teacherName)
                    }
                }
                val adapterTeacher = ArrayAdapter(this, android.R.layout.simple_spinner_item, teacherNames)
                adapterTeacher.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spTeacher.adapter = adapterTeacher
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load levels: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        // Menangani pemilihan tanggal ketika TextView diklik
        binding.etDay.setOnClickListener {
            showDatePickerDialog(this) { selectedDate ->
                binding.etDay.text = selectedDate
            }
        }

        // Menangani pemilihan tanggal ketika TextView diklik
        binding.etJoinDate.setOnClickListener {
            showDatePickerDialog(this) { selectedDate ->
                binding.etJoinDate.text = selectedDate
            }
        }

        // Mengatur adapter untuk spinner
        val optionTimes = arrayOf("10.00 WIB", "11.00 WIB", "12.00 WIB", "13.00 WIB", "14.00 WIB", "15.00 WIB", "16.00 WIB", "17.00 WIB")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, optionTimes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spTime.adapter = adapter

        binding.btnAddAddStudent.setOnClickListener {
            name = binding.etNameAdd.text.toString().trim()
            level = binding.spLevelAdd.selectedItem.toString().trim()
            joinDate = binding.etJoinDate.text.toString().trim()
            schDay = binding.etDay.text.toString().trim()
            schTime = binding.spTime.selectedItem.toString().trim()
            teacher = binding.spTeacher.selectedItem.toString().trim()
            dailyReportLink = binding.etDailyReportAdd.text.toString().trim()
            dayTime = "$schDay|$schTime"
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
                val studentLevelUp = joinDate?.let { levelUpDate(it) }

                getMaterialsForLevel(level) { materialsMap ->
                    val userMap = hashMapOf(
                        "studentId" to userId,
                        "levelId" to level,
                        "userId" to teacher,
                        "studentName" to name,
                        "studentAttendance" to attendance,
                        "studentAttendanceMaterials" to materialsMap,
                        "studentSchedule" to getStudentSchedule(joinDate, schDay, schTime),
                        "studentDailyReportLink" to dailyReportLink,
                        "studentAge" to age,
                        "studentJoinDate" to joinDate,
                        "studentDayTime" to dayTime,
                        "studentLevelUp" to studentLevelUp
                    )

                    usersCollection.document(userId).set(userMap).addOnSuccessListener {
                        Toast.makeText(this, "Successfully Added!!!", Toast.LENGTH_SHORT).show()

                        finish()
                    }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "There's something wrong", Toast.LENGTH_SHORT).show()
                            Log.d("RegisterActivity", "Error: ${e.message}")
                        }
                }
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun getMaterialsForLevel(levelId: String, callback: (HashMap<String, String>) -> Unit) {
        val materialsMap = hashMapOf<String, String>()

        db.collection("materials")
            .whereEqualTo("levelId", levelId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val materialsList = mutableListOf<Pair<String, String>>()
                for (document in querySnapshot) {
                    val materialId = document.getString("materialId")
                    val materialName = document.getString("materialName")
                    if (materialId != null && materialName != null) {
                        materialsList.add(Pair(materialId, materialName))
                    }
                }
                // Mengurutkan berdasarkan materialId
                materialsList.sortBy { it.first }

                // Memasukkan ke dalam HashMap dengan urutan yang sesuai
                materialsList.forEachIndexed { index, pair ->
                    materialsMap[(index + 1).toString()] = pair.second
                }

                callback(materialsMap)  // Panggil callback setelah data selesai diambil
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting materials: ", exception)
                callback(materialsMap)  // Tetap panggil callback meskipun ada kegagalan
            }
    }

    private fun getStudentSchedule(joinDate: String, schDay: String, schTime: String): HashMap<String, String> {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yy")
        val startDate = LocalDate.parse(joinDate, formatter)
        val dayOfWeek = when (schDay) {
            "Sunday" -> java.time.DayOfWeek.SUNDAY
            "Monday" -> java.time.DayOfWeek.MONDAY
            "Tuesday" -> java.time.DayOfWeek.TUESDAY
            "Wednesday" -> java.time.DayOfWeek.WEDNESDAY
            "Thursday" -> java.time.DayOfWeek.THURSDAY
            "Friday" -> java.time.DayOfWeek.FRIDAY
            "Saturday" -> java.time.DayOfWeek.SATURDAY
            else -> throw IllegalArgumentException("Invalid day of the week: $schDay")
        }

        val firstSession = if (startDate.dayOfWeek == dayOfWeek) {
            startDate
        } else {
            startDate.with(TemporalAdjusters.next(dayOfWeek))
        }

        val scheduleMap = hashMapOf<String, String>()
        for (i in 0 until 16) {
            val sessionDate = firstSession.plusWeeks(i.toLong())
            val sessionDateString = sessionDate.format(formatter)
            scheduleMap[(i + 1).toString()] = "$sessionDateString|$schTime"
        }

        return scheduleMap
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

    private fun levelUpDate(date: String): String {
        // Format tanggal
        val dateFormatter =
            DateTimeFormatter.ofPattern("dd-MM-yy")

        // Parse input date
        val theDate = LocalDate.parse(date, dateFormatter)

        // Tambah 6 bulan
        val newDate = theDate.plusMonths(6)

        // Cetak hasil
        return newDate.format(dateFormatter)
    }
}
