package com.example.digitrack.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.digitrack.databinding.ActivityDetailStudentBinding

class DetailStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStudentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(this, EditDetailStudentActivity::class.java))
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}