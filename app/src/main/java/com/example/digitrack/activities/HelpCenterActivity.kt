package com.example.digitrack.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.digitrack.databinding.ActivityHelpCenterBinding

class HelpCenterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpCenterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpCenterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}