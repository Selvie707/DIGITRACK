package com.example.digitrack.data

data class Students(
    val studentId: String = "",
    val levelId: String = "",
    val teacherName: String = "",
    val studentName: String = "",
    val studentAttendance: Int? = 0,
    val studentAttendanceMaterials: Map<String, String> = emptyMap(),
    val studentSchedule: Map<String, String> = emptyMap(),
    val studentDailyReportLink: String? = "",
    val studentAge: String? = "",
    val studentJoinDate: String? = "",
    val studentDayTime: String? = "",
    val studentCreatedAt: String? = "",
    val studentUpdatedAt: String? = "",
    val studentLevelUp: String? = ""
) {
    constructor() : this(
        studentId = "",
        levelId = "",
        teacherName = "",
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