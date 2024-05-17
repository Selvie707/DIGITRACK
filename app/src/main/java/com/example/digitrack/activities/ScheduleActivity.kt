package com.example.digitrack.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.digitrack.databinding.ActivityScheduleBinding

class ScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScheduleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}