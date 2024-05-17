package com.example.digitrack.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.digitrack.databinding.ActivityDailyReportBinding

class DailyReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDailyReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}