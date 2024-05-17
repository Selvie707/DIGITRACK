package com.example.digitrack.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.digitrack.R
import com.example.digitrack.databinding.ActivityNearestScheduleBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class NearestScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNearestScheduleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNearestScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = Firebase.auth.currentUser
        if (user != null) {
            Toast.makeText(this, "Login Berhasil!!!", Toast.LENGTH_SHORT).show()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.btn_session
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btn_session -> true
                R.id.btn_home -> {
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                    overridePendingTransition(0,0)
                    finish()
                    true
                }
                R.id.btn_profile -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    overridePendingTransition(0,0)
                    finish()
                    true
                }
                else -> false
            }
        }

        binding.tvJam.setOnClickListener {
            val url = "https://docs.google.com"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

    }
}