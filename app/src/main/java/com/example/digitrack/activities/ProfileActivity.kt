package com.example.digitrack.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.digitrack.R
import com.example.digitrack.databinding.ActivityProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.btn_profile
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btn_session -> {
                    startActivity(Intent(applicationContext, NearestScheduleActivity::class.java))
                    overridePendingTransition(0,0)
                    finish()
                    true
                }
                R.id.btn_home -> {
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                    overridePendingTransition(0,0)
                    finish()
                    true
                }
                R.id.btn_profile -> true
                else -> false
            }
        }

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
        binding.btnFeedback.setOnClickListener {
            startActivity(Intent(this, FeedbackActivity::class.java))
        }
        binding.btnHelpCenter.setOnClickListener {
            startActivity(Intent(this, HelpCenterActivity::class.java))
        }
        binding.btnLogout.setOnClickListener {
//            val editor = sharedPref.edit()
//
//            editor.clear()
//
//            editor.apply()

            Firebase.auth.signOut()

            startActivity(Intent(this, OnBoardingActivity::class.java))
            finish()
        }
    }
}