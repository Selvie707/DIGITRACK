package com.example.digitrack.data

data class Levels (
    val levelId: String = "",
    val curName: String = "",
    val levelSeq: Int = 0,
    val levelName: String = ""
) {
    constructor() : this("", "", 0, "")
}