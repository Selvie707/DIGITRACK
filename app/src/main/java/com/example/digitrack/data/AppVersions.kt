package com.example.digitrack.data

data class AppVersions(
    val avId: String,
    val avName: String,
    val avDescription:String? = null,
    val avCreatedAt: String,
    val avUpdatedAt: String,
)
