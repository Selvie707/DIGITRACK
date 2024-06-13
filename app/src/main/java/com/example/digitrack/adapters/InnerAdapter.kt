package com.example.digitrack.adapters

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.R
import com.example.digitrack.data.Students
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Calendar

class InnerAdapter(
    private val innerItemList: List<Students>,
    private val selectedDate: String, // Tambahkan parameter ini
    private val selectedTime: String // Tambahkan parameter ini
) : RecyclerView.Adapter<InnerAdapter.InnerViewHolder>() {
    inner class InnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        val tvStudentLevel: TextView = itemView.findViewById(R.id.tvStudentLevel)
        val tvStudentWeek: TextView = itemView.findViewById(R.id.tvStudentWeek)
        val tvStudentMaterial: TextView = itemView.findViewById(R.id.tvStudentMaterial)
        val ivTeacherColor: ImageView = itemView.findViewById(R.id.ivTeacherColor)
        val btnEdit: ImageView = itemView.findViewById(R.id.btnEdit)

        fun bind(student: Students, position: Int) {

            val context = itemView.context

            // Akses SharedPreferences
            val sharedPref = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val role = sharedPref.getString("role", "")

            // Ubah tampilan berdasarkan nilai role
            if (!role.equals("Admin")) {
                btnEdit.visibility = View.GONE
            }

            tvStudentName.text = student.studentName
            tvStudentLevel.text = student.levelId
//            tvStudentWeek.text = "Week ${student.studentSchedule.keys.firstOrNull().toString()}"
//            tvStudentMaterial.text = student.studentAttendanceMaterials.values.firstOrNull().toString()

            val keySchedule = student.studentSchedule.keys.find { key ->
                student.studentSchedule[key] == "$selectedDate|$selectedTime"
            }

            tvStudentWeek.text = "Week $keySchedule"

            // Ambil materi yang sesuai dengan posisi saat ini
            val studentMaterial = student.studentAttendanceMaterials[keySchedule] ?: "No material"

            tvStudentMaterial.text = studentMaterial ?: "No schedule" // Tampilkan materi atau pesan jika tidak ada jadwal

            // Ambil semua kunci jadwal siswa
            val scheduleKeys = student.studentSchedule.keys.toList()

            println(scheduleKeys)

            position+1

            // Set color based on userId
            when (student.userId) {
                "01" -> ivTeacherColor.setColorFilter(ContextCompat.getColor(itemView.context, R.color.yellow))
                "02" -> ivTeacherColor.setColorFilter(ContextCompat.getColor(itemView.context, R.color.purple))
            }

            // Handle edit button click
            btnEdit.setOnClickListener {
                showCustomDialog(itemView.context, student.studentId, keySchedule.toString())
            }
        }
    }

    private fun showCustomDialog(context: Context, studentId: String, studentScheduleKey: String) {
        // Gunakan LayoutInflater untuk memasukkan layout dialog kustom
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_change_schedule, null)

        // Inisialisasi elemen UI dalam layout dialog
        val tvDateTime = dialogView.findViewById<TextView>(R.id.tvChangeCalendar)
        val spinnerOptions = dialogView.findViewById<Spinner>(R.id.spChangeTime)
        val btnChange = dialogView.findViewById<TextView>(R.id.btnChange)
        val btnCancel = dialogView.findViewById<TextView>(R.id.btnCancel)

        // Mengatur adapter untuk spinner
        val options = arrayOf("10.00 WIB", "11.00 WIB", "13.00 WIB", "14.00 WIB", "15.00 WIB", "16.00 WIB", "17.00 WIB")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerOptions.adapter = adapter

        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)

        // Tampilkan dialog
        val dialog = dialogBuilder.create()

        // Menangani pemilihan tanggal ketika TextView diklik
        tvDateTime.setOnClickListener {
            showDatePickerDialog(context) { selectedDate ->
                tvDateTime.text = selectedDate
            }
        }

        btnChange.setOnClickListener {
            val selectedOption = spinnerOptions.selectedItem.toString()
            val dateTime = tvDateTime.text.toString()
            Toast.makeText(context, "$selectedOption $dateTime", Toast.LENGTH_SHORT).show()
            showAnotherDialog(context, selectedOption, dateTime, studentId, studentScheduleKey)
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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

    private fun showAnotherDialog(context: Context, previousOption: String, previousDate: String, studentId: String, studentScheduleKey: String) {
        // Inflate layout dialog kedua
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_schedule, null)

        // Inisialisasi komponen UI dalam dialog
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.rgOption)
        val btnSave = dialogView.findViewById<TextView>(R.id.tvSave)
        val btnCancel = dialogView.findViewById<TextView>(R.id.tvCancel)

        // Bangun AlertDialog dengan layout dialog kedua
        val dialogBuilder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
            .setView(dialogView)

        // Tampilkan dialog kedua
        val dialog = dialogBuilder.create()

        // Atur aksi untuk tombol "Save"
        btnSave.setOnClickListener {
            // Dapatkan radio button yang dipilih
            val selectedRadioButton = dialogView.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
            val selectedRadioText = selectedRadioButton.text.toString()

            // Hapus bagian "WIB" dari opsi waktu
            val formattedTime = previousOption.substringBefore(" ")

            println("$selectedRadioText, $formattedTime, $previousDate | $studentScheduleKey")

            if (selectedRadioText == "This Schedule") {
                updateThisSchedule(studentId, studentScheduleKey, "$previousDate|$formattedTime")
            } else if (selectedRadioText == "This and following schedule") {
                getStudentSchedule(studentId, studentScheduleKey.toInt(), previousDate, formattedTime)
            } else {
                println("Something went wrong")
            }

            // Tutup kedua dialog
            dialog.dismiss()
        }

        // Atur aksi untuk tombol "Cancel"
        btnCancel.setOnClickListener {
            // Tutup dialog kedua
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_inner_nearestschedule, parent, false)
        return InnerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
        holder.bind(innerItemList[position], position)
    }

    override fun getItemCount(): Int {
        return innerItemList.size
    }

    private fun updateThisSchedule(studentId: String, studentScheduleKey: String, studentScheduleValue: String) {
        val db = FirebaseFirestore.getInstance()

        // Perbarui nilai di Firestore
        db.collection("student").document(studentId).update("studentSchedule.$studentScheduleKey", studentScheduleValue)
            .addOnSuccessListener {
                // Penanganan sukses
                println("Nilai berhasil diperbarui!")
            }
            .addOnFailureListener { e ->
                // Penanganan kesalahan
                println("Gagal memperbarui nilai: $e")
            }
    }

    companion object {
        private fun getStudentSchedule(studentId: String, scheduleKey: Int, schDay: String, schTime: String) {
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yy")

            val joinDate = LocalDate.parse(schDay, formatter)

            val dayOfWeekName = when (joinDate.dayOfWeek) {
                java.time.DayOfWeek.SUNDAY -> "Sunday"
                java.time.DayOfWeek.MONDAY -> "Monday"
                java.time.DayOfWeek.TUESDAY -> "Tuesday"
                java.time.DayOfWeek.WEDNESDAY -> "Wednesday"
                java.time.DayOfWeek.THURSDAY -> "Thursday"
                java.time.DayOfWeek.FRIDAY -> "Friday"
                java.time.DayOfWeek.SATURDAY -> "Saturday"
            }

            println("The day of the week for $schDay is $dayOfWeekName")

            val startDate = LocalDate.parse(schDay, formatter)
            val dayOfWeek = when (dayOfWeekName) {
                "Sunday" -> java.time.DayOfWeek.SUNDAY
                "Monday" -> java.time.DayOfWeek.MONDAY
                "Tuesday" -> java.time.DayOfWeek.TUESDAY
                "Wednesday" -> java.time.DayOfWeek.WEDNESDAY
                "Thursday" -> java.time.DayOfWeek.THURSDAY
                "Friday" -> java.time.DayOfWeek.FRIDAY
                "Saturday" -> java.time.DayOfWeek.SATURDAY
                else -> throw IllegalArgumentException("Invalid day of the week: $dayOfWeekName")
            }

            val firstSession = if (startDate.dayOfWeek == dayOfWeek) {
                startDate
            } else {
                startDate.with(TemporalAdjusters.next(dayOfWeek))
            }

            println("Schedule Key: $scheduleKey")

            val scheduleMap = hashMapOf<String, String>()
            var j = 0
            for (i in scheduleKey until 16) {
                val sessionDate = firstSession.plusWeeks(j.toLong())
                val sessionDateString = sessionDate.format(formatter)
                scheduleMap["studentSchedule." + (i).toString()] = "$sessionDateString|$schTime"
                j++
            }

            // Menambahkan data dengan key startKey-1
//            scheduleMap["studentSchedule." + (scheduleKey).toString()] = "$schDay|$schTime WIB"

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
                .update("studentDayTime", "$dayOfWeekName|$schTime WIB")
                .addOnSuccessListener {
                    // Penanganan sukses
                    println("Jadwal berhasil diperbarui!")
                }
                .addOnFailureListener { e ->
                    // Penanganan kesalahan
                    println("Gagal memperbarui jadwal: $e")
                }

            // Cetak hasilnya untuk pengecekan
            println("sch map: $scheduleMap")
        }
    }
}