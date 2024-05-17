package com.example.digitrack.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.digitrack.databinding.ActivityAddNewStudentBinding

class AddNewStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewStudentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddAddStudent.setOnClickListener {
            val intent = Intent(this, AttendanceActivity::class.java)
            intent.putExtra("key", "")
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}