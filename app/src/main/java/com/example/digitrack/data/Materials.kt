package com.example.digitrack.data

data class Materials (
    val materialId: String = "",
    val levelId: String = "",
    val materialName: String = ""
) {
    // No-argument constructor required by Firestore
    constructor() : this("", "", "")
}