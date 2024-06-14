package com.example.digitrack.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.digitrack.databinding.ActivityFeedbackBinding
import com.google.firebase.firestore.FirebaseFirestore

class FeedbackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedbackBinding
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val usersCollection = db.collection("feedback")
        val sharedPref = applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        var checkedStars = 0

        val starCheckboxes = listOf(
            binding.cbRateOne,
            binding.cbRateTwo,
            binding.cbRateThree,
            binding.cbRateFour,
            binding.cbRateFive
        )

        starCheckboxes.forEachIndexed { index, checkbox ->
            checkbox.setOnClickListener {
                checkedStars = index + 1 // Increase by 1 since index starts from 0
                starCheckboxes.forEachIndexed { innerIndex, innerCheckbox ->
                    innerCheckbox.isChecked = innerIndex <= index
                }
            }
        }

        binding.btnSave.setOnClickListener {
            val comment = binding.etCriticsugges.text.toString()
            val name = sharedPref.getString("name", "")

            val userId = usersCollection.document().id

            val userMap = hashMapOf(
                "feedbackId" to userId,
                "userName" to name,
                "feedbackStarRate" to checkedStars,
                "feedbackText" to comment,
            )

            usersCollection.document(userId).set(userMap).addOnSuccessListener {
                Toast.makeText(this, "Successfully Added!!!", Toast.LENGTH_SHORT).show()
                finish()
            }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "There's something wrong", Toast.LENGTH_SHORT).show()
                    Log.d("RegisterActivity", "Error: ${e.message}")
                }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}