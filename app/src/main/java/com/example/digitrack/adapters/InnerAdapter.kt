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
import java.util.Calendar

class InnerAdapter(private val innerItemList: List<Students>) : RecyclerView.Adapter<InnerAdapter.InnerViewHolder>() {

    inner class InnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        val tvStudentLevel: TextView = itemView.findViewById(R.id.tvStudentLevel)
        val tvStudentWeek: TextView = itemView.findViewById(R.id.tvStudentWeek)
        val tvStudentMaterial: TextView = itemView.findViewById(R.id.tvStudentMaterial)
        val ivTeacherColor: ImageView = itemView.findViewById(R.id.ivTeacherColor)
        val btnEdit: ImageView = itemView.findViewById(R.id.btnEdit)

        fun bind(student: Students) {
            tvStudentName.text = student.studentName
            tvStudentLevel.text = student.levelId
//            tvStudentWeek.text = "Week ${student.studentSchedule.keys.firstOrNull().toString()}"
//            tvStudentMaterial.text = student.studentAttendanceMaterials.values.firstOrNull().toString()

            // Tampilkan semua jadwal untuk setiap siswa
            val scheduleKey = student.studentSchedule.keys.joinToString(", ") // Gabungkan semua kunci menjadi satu string
            tvStudentWeek.text = "Week $scheduleKey"

            // Menampilkan materi dari jadwal pertama
            val studentMaterial = student.studentAttendanceMaterials.values.firstOrNull()
            tvStudentMaterial.text = studentMaterial ?: "No schedule" // Tampilkan materi atau pesan jika tidak ada jadwal

            // Ambil semua kunci jadwal siswa
            val scheduleKeys = student.studentSchedule.keys.toList()

            println(scheduleKeys)

            // Set color based on userId
            when (student.userId) {
                "01" -> ivTeacherColor.setColorFilter(ContextCompat.getColor(itemView.context, R.color.yellow))
                "02" -> ivTeacherColor.setColorFilter(ContextCompat.getColor(itemView.context, R.color.purple))
            }

            // Handle edit button click
            btnEdit.setOnClickListener {
                showCustomDialog(itemView.context, student.studentId, student.studentSchedule.keys.firstOrNull().toString())
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
        val options = arrayOf("10.00 WIB", "11.00 WIB", "12.00 WIB")
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

            updateThisSchedule(studentId, studentScheduleKey, "$previousDate|$formattedTime")

            println("$selectedRadioText, $formattedTime, $previousDate | $studentScheduleKey")

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
        holder.bind(innerItemList[position])
    }

    override fun getItemCount(): Int {
        return innerItemList.size
    }

    private fun updateThisSchedule(studentId: String, studentScheduleKey: String, studentScheduleValue: String) {
        val db = FirebaseFirestore.getInstance()

//        val updates = hashMapOf(
//            oldKey to "25-05-24|11.00"
//        )

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

//        db.collection("student").document(studentId).get()
//            .addOnSuccessListener { document ->
//                if (document != null) {
//                    val studentSchedule = document.get("studentSchedule") as? MutableMap<String, String>
//
//                    Log.d("UPDATE SCHEDULE", studentSchedule.toString())
//
//                    if (studentSchedule != null && studentSchedule.containsKey(oldKey)) {
//                        val value = studentSchedule[oldKey] // Ambil nilai dari oldKey
////                        studentSchedule.remove(oldKey) // Hapus oldKey dari map
//
//                        Log.d("UPDATE SCHEDULE IN", studentSchedule.toString()+value)
//
//                        // Pertama, hapus oldKey dari Firestore
//                        val updates = hashMapOf<String, Any>(
//                            "studentSchedule.$oldKey" to FieldValue.delete()
//                        )
//
//                        Log.d("UPDATE SCHEDULE IN", studentSchedule.toString())
//
//                        db.collection("students").document(studentId)
//                            .update(updates)
//                            .addOnSuccessListener {
//                                Log.d("Firestore", "Successfully deleted key from map")
//
//                                val newData = hashMapOf(newKey to "2")
//
//                                // Setelah oldKey dihapus, tambahkan newKey ke Firestore
//                                db.collection("students").document(studentId)
//                                    .set(mapOf("studentSchedule" to newData), /* Set options */  SetOptions.merge())
//                                    .addOnSuccessListener {
//                                        Log.d("CHANGE SCHEDULE", "Success")
//                                    }
//                                    .addOnFailureListener { e ->
//                                        Log.d("CHANGE SCHEDULE", "Failed to add new key", e)
//                                    }
//                            }
//                            .addOnFailureListener { e ->
//                                Log.w("Firestore", "Error deleting key from map", e)
//                            }
//
//                    } else {
//                        Log.d("CHANGE SCHEDULE", "Key Not Found")
//                    }
//                } else {
//                    Log.d("CHANGE SCHEDULE", "Document Not Found")
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.d("CHANGE SCHEDULE", "Failed to fetch the document", e)
//            }
    }
}