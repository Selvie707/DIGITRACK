package com.example.digitrack.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.R
import com.example.digitrack.activities.DetailStudentActivity
import com.example.digitrack.data.Students
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class AttendancesAdapter(
    private var attendancesList: List<Students>,
    private var currentMonth: Int,
    private var currentYear: Int,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<AttendancesAdapter.AttendanceViewHolder>() {

    val db = FirebaseFirestore.getInstance()

    inner class AttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStudentName: TextView = itemView.findViewById(R.id.tvAttendanceName)
        private val tvMaterial: TextView = itemView.findViewById(R.id.tvMaterial)
        private val ivWeekI: ImageView = itemView.findViewById(R.id.ivWeekI)
        private val ivWeekII: ImageView = itemView.findViewById(R.id.ivWeekII)
        private val ivWeekIII: ImageView = itemView.findViewById(R.id.ivWeekIII)
        private val ivWeekIV: ImageView = itemView.findViewById(R.id.ivWeekIV)

        // Variabel boolean untuk menyimpan status minggu
        private var weekI: Boolean = false
        private var weekII: Boolean = false
        private var weekIII: Boolean = false
        private var weekIV: Boolean = false

        fun bind(attendance: Students) {
            val studentId = attendance.studentId
            val studentName = attendance.studentName
            val studentLevel = attendance.levelId
            val joinDate = LocalDate.parse(attendance.studentJoinDate, DateTimeFormatter.ofPattern("dd-MM-yy"))
            val studentLevelUp = attendance.studentLevelUp
            val totalAttendance = attendance.studentAttendance ?: 0
            val dayTime = attendance.studentDayTime
            val currentLevel = studentLevelUp(studentLevelUp!!, studentId, studentLevel, dayTime!!)

            val studentDetailText = "$studentLevel - $studentName"
            tvStudentName.text = studentDetailText

            // Reset the visibility of tvMaterial and booleans
            tvMaterial.visibility = View.GONE
            weekI = false
            weekII = false
            weekIII = false
            weekIV = false

            // Menghitung checklist yang dibutuhkan untuk bulan tertentu
            val (checklistForMonth, meetingMap) = calculateChecklistsII(totalAttendance, joinDate, currentMonth, currentYear)

            // Menampilkan checklist sesuai dengan perhitungan
            setChecklists(ivWeekI, ivWeekII, ivWeekIII, ivWeekIV, checklistForMonth)

            // Mengatur OnClickListener untuk ivWeekI
            ivWeekI.setOnClickListener {
                weekI = !weekI
                if (weekI) {
                    weekII = false
                    weekIII = false
                    weekIV = false
                    val meetingNumber = meetingMap[1] ?: return@setOnClickListener
                    showMaterial(tvMaterial, attendance, meetingNumber)
                } else {
                    tvMaterial.visibility = View.GONE
                }
            }

            // Mengatur OnClickListener untuk ivWeekII
            ivWeekII.setOnClickListener {
                weekII = !weekII
                if (weekII) {
                    weekI = false
                    weekIII = false
                    weekIV = false
                    val meetingNumber = meetingMap[2] ?: return@setOnClickListener
                    showMaterial(tvMaterial, attendance, meetingNumber)
                } else {
                    tvMaterial.visibility = View.GONE
                }
            }

            // Mengatur OnClickListener untuk ivWeekIII
            ivWeekIII.setOnClickListener {
                weekIII = !weekIII
                if (weekIII) {
                    weekI = false
                    weekII = false
                    weekIV = false
                    val meetingNumber = meetingMap[3] ?: return@setOnClickListener
                    showMaterial(tvMaterial, attendance, meetingNumber)
                } else {
                    tvMaterial.visibility = View.GONE
                }
            }

            // Mengatur OnClickListener untuk ivWeekIV
            ivWeekIV.setOnClickListener {
                weekIV = !weekIV
                if (weekIV) {
                    weekI = false
                    weekII = false
                    weekIII = false
                    val meetingNumber = meetingMap[4] ?: return@setOnClickListener
                    showMaterial(tvMaterial, attendance, meetingNumber)
                } else {
                    tvMaterial.visibility = View.GONE
                }
            }

            tvStudentName.setOnClickListener {
                // Panggil fungsi onItemClick dan kirimkan posisi item serta konteks
                onItemClick(adapterPosition)

                // Intent ke Activity lain
                val context = tvStudentName.context
                val intent = Intent(context, DetailStudentActivity::class.java).apply {
                    putExtra("studentId", attendance.studentId)
                    putExtra("studentName", attendance.studentName)
                    putExtra("studentLevel", currentLevel)
                    putExtra("studentAge", attendance.studentAge)
                    putExtra("studentDayTime", attendance.studentDayTime)
                    putExtra("studentAttendance", attendance.studentAttendance.toString())
                    putExtra("studentTeacher", attendance.teacherName)
                    putExtra("studentDailyReportLink", attendance.studentDailyReportLink)
                    putExtra("studentJoinDate", attendance.studentJoinDate)
                    putExtra("studentLevelUp", attendance.studentLevelUp)
                }
                context.startActivity(intent)
            }
        }

        private fun calculateChecklistsII(totalAttendance: Int, joinDate: LocalDate, currentMonth: Int, currentYear: Int): Pair<Int, Map<Int, Int>> {
            var remainingAttendance = totalAttendance
            var yearCounter = joinDate.year
            var monthCounter = joinDate.monthValue
            var checklistForMonth = 0
            var meetingNumber = 1
            val meetingMap = mutableMapOf<Int, Int>()

            while (remainingAttendance > 0) {
                val checklistsThisMonth = if (remainingAttendance >= 4) 4 else remainingAttendance
                if (yearCounter == currentYear && monthCounter == currentMonth) {
                    checklistForMonth = checklistsThisMonth
                    for (week in 1..checklistsThisMonth) {
                        meetingMap[week] = meetingNumber
                        meetingNumber++
                    }
                    break
                }
                for (week in 1..checklistsThisMonth) {
                    meetingMap[week] = meetingNumber
                    meetingNumber++
                }
                remainingAttendance -= checklistsThisMonth
                if (monthCounter == 12) {
                    monthCounter = 1
                    yearCounter++
                } else {
                    monthCounter++
                }
            }

            return Pair(checklistForMonth, meetingMap)
        }

        private fun setChecklists(ivWeekI: ImageView, ivWeekII: ImageView, ivWeekIII: ImageView, ivWeekIV: ImageView, checklists: Int) {
            val colorActive = ContextCompat.getColor(itemView.context, R.color.purple)
            val colorInactive = ContextCompat.getColor(itemView.context, R.color.white)

            ivWeekI.setColorFilter(if (checklists >= 1) colorActive else colorInactive)
            ivWeekII.setColorFilter(if (checklists >= 2) colorActive else colorInactive)
            ivWeekIII.setColorFilter(if (checklists >= 3) colorActive else colorInactive)
            ivWeekIV.setColorFilter(if (checklists >= 4) colorActive else colorInactive)
        }

        private fun showMaterial(tvMaterial: TextView, student: Students, meetingNumber: Int) {
            val studentMaterial = student.studentAttendanceMaterials[meetingNumber.toString()] ?: "No material"
            tvMaterial.text = studentMaterial
            tvMaterial.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_attendance, parent, false)
        return AttendanceViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return attendancesList.size
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        holder.bind(attendancesList[position])
    }

    // Fungsi untuk memperbarui data dan menyegarkan tampilan
    fun updateData(newList: List<Students>, newMonth: Int, newYear: Int) {
        attendancesList = newList
        currentMonth = newMonth
        currentYear = newYear
        notifyDataSetChanged()
    }

    private fun studentLevelUp(levelUpDate: String, studentId: String, currentLevel: String, dayTime: String): String {
        // Ubah format tanggal yang diinput ke LocalDate
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yy")
        val parsedInputDate = LocalDate.parse(levelUpDate, formatter)
        val nextLevel: String
        val day = dayTime.split("|")[0]
        val time = dayTime.split("|")[1].split(" ")[0]

        // Tanggal hari ini
        val currentDate = LocalDate.now()

        // Periksa apakah tanggal yang diinput lebih kecil atau sama dengan tanggal hari ini
        if (parsedInputDate.isBefore(currentDate) || parsedInputDate.isEqual(currentDate)) {
            println("Tanggal yang diinput lebih kecil atau sama dengan tanggal hari ini.")
            // Lakukan tindakan yang sesuai di sini

            nextLevel = when (currentLevel) {
                "DK3LC1L1" -> "DK3LC1L2"
                "DK2J1" -> "DK2J2"
                "DK2J2" -> "DK2T1"
                // Tambahkan kasus lain jika diperlukan
                else -> {
                    // Tambahkan tindakan yang sesuai jika level tidak cocok dengan kasus yang ada
                    println("Level tidak dikenali.")
                    ""
                }
            }

            println("nexLev: $nextLevel")

            // Format tanggal
            val dateFormatter =
                DateTimeFormatter.ofPattern("dd-MM-yy")

            // Parse input date
            val theDate = LocalDate.parse(levelUpDate, dateFormatter)

            var newDate = theDate.plusMonths(1)

            getMaterialsForLevel(nextLevel) { materialMap ->
                val curriculum = nextLevel.substring(0,3)
                println("nexLev: $nextLevel")
                val updates = hashMapOf(
                    "levelId" to nextLevel,
                    "studentJoinDate" to newDate.format(dateFormatter),
                    "studentLevelUp" to levelUpDate(curriculum, newDate.format(dateFormatter)),
                    "studentAttendanceMaterials" to materialMap,
                    "studentSchedule" to getStudentSchedule(newDate.format(dateFormatter), day, time)
                )

                // Perbarui nilai di Firestore
                db.collection("student").document(studentId).update(updates as Map<String, Any>)
                    .addOnSuccessListener {
                        // Penanganan sukses
                        println("Nilai berhasil diperbarui!")
                    }
                    .addOnFailureListener { e ->
                        // Penanganan kesalahan
                        println("Gagal memperbarui nilai: $e")
                    }
            }
        } else {
            nextLevel = currentLevel
            println("Tanggal yang diinput lebih besar dari tanggal hari ini.")
            // Lakukan tindakan yang sesuai di sini
        }

        return nextLevel
    }

    private fun getMaterialsForLevel(levelId: String, callback: (HashMap<String, String>) -> Unit) {
        val materialsMap = hashMapOf<String, String>()

        db.collection("materials")
            .whereEqualTo("levelId", levelId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val materialsList = mutableListOf<Pair<Long, String>>()
                for (document in querySnapshot) {
                    val materialId = document.getLong("materialId")
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

                callback(materialsMap)  // Panggil callback setelah data selesai diambil
            }
    }

    private fun levelUpDate(curriculum: String, date: String): String {
        // Format tanggal
        val dateFormatter =
            DateTimeFormatter.ofPattern("dd-MM-yy")

        // Parse input date
        val theDate = LocalDate.parse(date, dateFormatter)

        var newDate: LocalDate = if (curriculum == "DK3") {
            theDate.plusMonths(3)
        } else {
            theDate.plusMonths(11)
        }

        // Cetak hasil
        return newDate.format(dateFormatter)
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
}