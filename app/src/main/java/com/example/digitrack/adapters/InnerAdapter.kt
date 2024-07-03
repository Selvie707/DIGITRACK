package com.example.digitrack.adapters

import android.Manifest
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
    private val selectedDate: String,
    private val selectedTime: String
) : RecyclerView.Adapter<InnerAdapter.InnerViewHolder>() {
    inner class InnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        private val tvStudentLevel: TextView = itemView.findViewById(R.id.tvStudentLevel)
        private val tvStudentWeek: TextView = itemView.findViewById(R.id.tvStudentWeek)
        private val tvStudentMaterial: TextView = itemView.findViewById(R.id.tvStudentMaterial)
        private val ivTeacherColor: ImageView = itemView.findViewById(R.id.ivTeacherColor)
        private val btnEdit: ImageView = itemView.findViewById(R.id.btnEdit)

        fun bind(student: Students, position: Int) {

            val context = itemView.context

            val sharedPref = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val role = sharedPref.getString("role", "")

            if (!role.equals("Admin")) {
                btnEdit.visibility = View.GONE
            }

            tvStudentName.text = student.studentName
            tvStudentLevel.text = student.levelId

            val keySchedule = student.studentSchedule.keys.find { key ->
                student.studentSchedule[key] == "$selectedDate|$selectedTime"
            }

            val studentWeekText = "Week $keySchedule"
            tvStudentWeek.text = studentWeekText

            val studentMaterial = student.studentAttendanceMaterials[keySchedule] ?: "No material"

            tvStudentMaterial.text = studentMaterial

            position+1

            when (student.teacherName) {
                "Axel" -> ivTeacherColor.setColorFilter(ContextCompat.getColor(itemView.context, R.color.yellow))
                "Via" -> ivTeacherColor.setColorFilter(ContextCompat.getColor(itemView.context, R.color.purple))
            }

            btnEdit.setOnClickListener {
                showCustomDialog(itemView.context, student.studentId, keySchedule.toString(), student.studentName)
            }
        }
    }

    private fun showCustomDialog(context: Context, studentId: String, studentScheduleKey: String, studentName: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_change_schedule, null)

        val tvDateTime = dialogView.findViewById<TextView>(R.id.tvChangeCalendar)
        val spinnerOptions = dialogView.findViewById<Spinner>(R.id.spChangeTime)
        val btnChange = dialogView.findViewById<TextView>(R.id.btnChange)
        val btnCancel = dialogView.findViewById<TextView>(R.id.btnCancel)

        val options = arrayOf("Time", "10.00 WIB", "11.00 WIB", "12.00 WIB", "13.00 WIB", "14.00 WIB", "15.00 WIB", "16.00 WIB", "17.00 WIB")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerOptions.adapter = adapter

        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)

        val dialog = dialogBuilder.create()

        tvDateTime.setOnClickListener {
            showDatePickerDialog(context) { selectedDate ->
                tvDateTime.text = selectedDate
            }
        }

        btnChange.setOnClickListener {
            val selectedOption = spinnerOptions.selectedItem.toString().trim()
            val dateTime = tvDateTime.text.toString().trim()

            println("DateTime: $dateTime")

            if (dateTime == "DAY" || dateTime.isEmpty()) {
                tvDateTime.error = "Pick the date"
            } else if (selectedOption == "Time") {
                Toast.makeText(context, "Pick the time", Toast.LENGTH_SHORT).show()
            } else {
                showAnotherDialog(context, selectedOption, dateTime, studentId, studentScheduleKey, studentName)
                dialog.dismiss()
            }
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
            val formattedYear = (selectedYear % 100).toString().padStart(2, '0')
            val formattedDate = "${selectedDay.toString().padStart(2, '0')}-${(selectedMonth + 1).toString().padStart(2, '0')}-$formattedYear"
            onDateSelected(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showAnotherDialog(context: Context, previousOption: String, previousDate: String, studentId: String, studentScheduleKey: String, studentName: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_schedule, null)

        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.rgOption)
        val rbThis = dialogView.findViewById<RadioButton>(R.id.rbThisSchedule)
        val btnSave = dialogView.findViewById<TextView>(R.id.tvSave)
        val btnCancel = dialogView.findViewById<TextView>(R.id.tvCancel)

        rbThis.isChecked = true

        val dialogBuilder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
            .setView(dialogView)

        val dialog = dialogBuilder.create()

        btnSave.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId

            if (selectedRadioButtonId == -1) {
                Toast.makeText(context, "Please select one option", Toast.LENGTH_SHORT).show()
            } else {
                val selectedRadioButton = dialogView.findViewById<RadioButton>(selectedRadioButtonId)
                val selectedRadioText = selectedRadioButton.text.toString().trim()

                val formattedTime = previousOption.substringBefore(" ")

                when (selectedRadioText) {
                    "This Schedule" -> {
                        updateThisSchedule(studentId, studentScheduleKey, "$previousDate|$formattedTime")
                        showNotification(context, "Schedule changed", "$studentName moved to $previousDate|$formattedTime")
                    }
                    "This and following schedule" -> {
                        getStudentSchedule(studentId, studentScheduleKey.toInt(), previousDate, formattedTime)
                        val formatter = DateTimeFormatter.ofPattern("dd-MM-yy")
                        val joinDate = LocalDate.parse(previousDate, formatter)

                        val dayOfWeekName = when (joinDate.dayOfWeek) {
                            java.time.DayOfWeek.SUNDAY -> "Sunday"
                            java.time.DayOfWeek.MONDAY -> "Monday"
                            java.time.DayOfWeek.TUESDAY -> "Tuesday"
                            java.time.DayOfWeek.WEDNESDAY -> "Wednesday"
                            java.time.DayOfWeek.THURSDAY -> "Thursday"
                            java.time.DayOfWeek.FRIDAY -> "Friday"
                            java.time.DayOfWeek.SATURDAY -> "Saturday"
                        }
                        showNotification(context, "Notification", "$studentName changed day to $dayOfWeekName at $formattedTime")
                    }
                    else -> {
                        println("Something went wrong")
                    }
                }

                dialog.dismiss()
            }
        }

        btnCancel.setOnClickListener {
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

        db.collection("student").document(studentId).update("studentSchedule.$studentScheduleKey", studentScheduleValue)
            .addOnSuccessListener {
                println("Value updated sucessfully!")
            }
            .addOnFailureListener { e ->
                println("Value update failed: $e")
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

            val scheduleMap = hashMapOf<String, String>()
            var j = 0
            for (i in scheduleKey until 16) {
                val sessionDate = firstSession.plusWeeks(j.toLong())
                val sessionDateString = sessionDate.format(formatter)
                scheduleMap["studentSchedule." + (i).toString()] = "$sessionDateString|$schTime"
                j++
            }

            val db = FirebaseFirestore.getInstance()

            db.collection("student").document(studentId)
                .update(scheduleMap as Map<String, Any>)
                .addOnSuccessListener {
                    println("Schedule updated sucessfully!")
                }
                .addOnFailureListener { e ->
                    println("Schedule update failed: $e")
                }

            db.collection("student").document(studentId)
                .update("studentDayTime", "$dayOfWeekName|$schTime WIB")
                .addOnSuccessListener {
                    println("Schedule updated sucessfully!")
                }
                .addOnFailureListener { e ->
                    println("Schedule update failed: $e")
                }
        }
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val channelId = "schedule_change_channel"
        val channelName = "Schedule Change Notification"

        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = "Schedule Has Changed"
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_calendar)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(1, builder.build())
        }
    }
}