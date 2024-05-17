package com.example.digitrack.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.digitrack.databinding.ActivityOnBoardingBinding

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val hasName = sharedPref.contains("name")

        if (hasName) {
            startActivity(Intent(this, NearestScheduleActivity::class.java))
        }

        binding.btnLoginOnboarding.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.tvRegisterOnboarding.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}