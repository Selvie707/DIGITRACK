package com.example.digitrack.data

data class Students(
    val studentId: String = "",
    val levelId: String = "",
    val userId: String = "",
    val studentName: String = "",
    val studentAttendance: Int? = 0,
    val studentAttendanceMaterials: Map<String, String> = emptyMap(), // <pertemuan ke, materialsId>
    val studentSchedule: Map<String, String> = emptyMap(),            // <date time, studentAttendanceMaterials>
    val studentDailyReportLink: String? = "",
    val studentAge: String? = "",
    val studentJoinDate: String? = "",
    val studentDayTime: String? = "",
    val studentCreatedAt: String? = "",
    val studentUpdatedAt: String? = "",
    val studentLevelUp: String? = ""
) {
    // No-argument constructor required by Firestore
    constructor() : this(
        studentId = "",
        levelId = "",
        userId = "",
        studentName = "",
        studentAttendance = 0,
        studentAttendanceMaterials = emptyMap(),
        studentSchedule = emptyMap(),
        studentDailyReportLink = "",
        studentAge = "",
        studentJoinDate = "",
        studentDayTime = "",
        studentCreatedAt = "",
        studentUpdatedAt = "",
        studentLevelUp = ""
    )
}