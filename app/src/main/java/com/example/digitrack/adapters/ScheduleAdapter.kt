package com.example.digitrack.adapters

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.R
import com.example.digitrack.data.Students
import java.util.Calendar

class ScheduleAdapter(
    private val scheduleList: List<Students>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        private val tvStudentLevel: TextView = itemView.findViewById(R.id.tvStudentLevel)
        private val tvStudentWeek: TextView = itemView.findViewById(R.id.tvStudentWeek)
        private val tvStudentMaterial: TextView = itemView.findViewById(R.id.tvStudentMaterial)
        private val btnEdit: ImageView = itemView.findViewById(R.id.btnEdit)

        fun bind(schedule: Students) {
            val studentWeek = schedule.studentAttendanceMaterials.keys.firstOrNull().toString()
            val data = schedule.studentSchedule.toSortedMap().keys.firstOrNull().toString()

            // Memisahkan string menggunakan karakter '|'
            val parts = data.split('|')

            // Mengambil bagian kedua setelah pemisahan
            val time = parts.getOrNull(1)

            tvTime.text = "JAM $time"
            tvStudentName.text = schedule.studentName
            tvStudentLevel.text = schedule.levelId
            tvStudentWeek.text = "Week $studentWeek"
            tvStudentMaterial.text = schedule.studentAttendanceMaterials.values.firstOrNull().toString()

            itemView.setOnClickListener {
                onItemClick(adapterPosition)
                val url = schedule.studentDailyReportLink
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                itemView.context.startActivity(intent)
            }

            btnEdit.setOnClickListener {
                showCustomDialog(itemView.context)
            }
        }
    }

    private fun showCustomDialog(context: Context) {
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
            showAnotherDialog(context, selectedOption, dateTime)
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
            val selectedDate = "${selectedDay.toString().padStart(2, '0')}/${(selectedMonth + 1).toString().padStart(2, '0')}/$selectedYear"
            onDateSelected(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showAnotherDialog(context: Context, previousOption: String, previousDate: String) {
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

            // Tampilkan toast dengan informasi tersebut
            Toast.makeText(context, "$selectedRadioText, $previousOption, $previousDate", Toast.LENGTH_SHORT).show()

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_schedule, parent, false)
        return ScheduleViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return scheduleList.size
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(scheduleList[position])
    }
}