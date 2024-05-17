package com.example.digitrack.data

data class Levels (
    val levelId: String = "",
    val curName: String = "",
    val levelName: String = ""
) {
    // No-argument constructor required by Firestore
    constructor() : this("", "", "")
}