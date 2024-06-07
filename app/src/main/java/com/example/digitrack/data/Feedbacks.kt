package com.example.digitrack.data

data class Feedbacks(
    val userName: String,
    val feedbackId: String,
    val feedbackStarRate: Int,
    val feedbackText: String,
    val feedbackCreatedAt: String,
    val feedbackUpdatedAt: String,
) {
    constructor() : this("", "", 0, "", "", "")
}
