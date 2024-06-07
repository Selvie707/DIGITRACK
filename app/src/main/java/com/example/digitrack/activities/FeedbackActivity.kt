package com.example.digitrack.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.digitrack.MailSender
import com.example.digitrack.databinding.ActivityFeedbackBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            println("$checkedStars $comment")
            val name = sharedPref.getString("name", "")

            val userId = usersCollection.document().id

            val userMap = hashMapOf(
                "feedbackId" to userId,
                "userName" to name,
                "feedbackStarRate" to checkedStars,
                "feedbackText" to comment,
            )

            usersCollection.document(userId!!).set(userMap).addOnSuccessListener {
                Toast.makeText(this, "Successfully Added!!!", Toast.LENGTH_SHORT).show()
                finish()
            }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "There's something wrong", Toast.LENGTH_SHORT).show()
                    Log.d("RegisterActivity", "Error: ${e.message}")
                }

//            val recipient = "recipient@example.com"
//            val subject = "Subject of the Email"
//            val body = "Body of the Email"
//
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    MailSender.sendMail(recipient, subject, body)
//                    runOnUiThread {
//                        Toast.makeText(this@FeedbackActivity, "Email Sent Successfully", Toast.LENGTH_LONG).show()
//                    }
//                } catch (e: Exception) {
//                    runOnUiThread {
//                        println("Failed to Send Email: ${e.message}")
//                    }
//                }
//            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}