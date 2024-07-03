package com.example.digitrack.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.digitrack.R
import com.example.digitrack.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val role = sharedPref.getString("role", "")
        val name = sharedPref.getString("name", "")

        val hiUserText = "Hi, $name"
        binding.tvHaiUser.text = hiUserText

        if (!role.equals("Teacher")) {
            binding.tvReady.text = "Admin tasks at your service!"
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.btn_home
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btn_session -> {
                    startActivity(Intent(applicationContext, NearestScheduleActivity::class.java))
                    overridePendingTransition(0,0)
                    finish()
                    true
                }
                R.id.btn_home -> true
                R.id.btn_profile -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    overridePendingTransition(0,0)
                    finish()
                    true
                }
                else -> false
            }
        }

        binding.llSchedule.setOnClickListener {
            startActivity(Intent(this, ScheduleActivity::class.java))
        }
        binding.llAttendance.setOnClickListener {
            startActivity(Intent(this, AttendanceActivity::class.java))
        }
        binding.llMaterials.setOnClickListener {
            startActivity(Intent(this, MaterialsActivity::class.java))
        }
        binding.llDailyReport.setOnClickListener {
            startActivity(Intent(this, DailyReportActivity::class.java))
        }
    }
}