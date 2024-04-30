package com.example.digitrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.digitrack.databinding.ActivityEditDetailStudentBinding

class EditDetailStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditDetailStudentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDetailStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSaveEditStudent.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("key", "")
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}