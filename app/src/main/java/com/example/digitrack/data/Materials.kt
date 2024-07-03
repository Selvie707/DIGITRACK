package com.example.digitrack.data

data class Materials (
    val materialId: Int = 0,
    val levelId: String = "",
    val materialName: String = ""
) {
    constructor() : this(0, "", "")
}