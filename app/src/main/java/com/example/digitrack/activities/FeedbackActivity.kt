package com.example.digitrack.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.digitrack.databinding.ActivityFeedbackBinding

class FeedbackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("key", "")
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}