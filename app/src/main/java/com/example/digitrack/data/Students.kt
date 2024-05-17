package com.example.digitrack.data

data class Students (
    val studentId: String,
    val levelId: String,
    val userId: String,
    val studentName: String,
    val studentAttendance: Int? = null,
    val studentAttendanceMaterials: Map<Int, String>? = emptyMap(),   // <pertemuan ke, materialsId>
    val studentSchedule: Map<String, String>? = emptyMap(),           // <date time, studentAttendanceMaterials>
    val studentDailyReportLink: String? = null,
    val studentCreatedAt: String? = null,
    val studentUpdatedAt: String? = null,
) {
    // No-argument constructor required by Firestore
    constructor() : this("", "", "", "", 0, emptyMap(), emptyMap(), "", "", "")
}
