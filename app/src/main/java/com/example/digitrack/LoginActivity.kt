package com.example.digitrack

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.digitrack.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        binding.btnLoginOnboarding.setOnClickListener {
            email = binding.etEmailLogin.text.toString()
            password = binding.etPasswordLogin.text.toString()

            if (email.isBlank() || !email.contains("@") || !email.contains(".")) {
                binding.etEmailLogin.error = "Fill proper email"
            } else if (password.isBlank()) {
            binding.etPasswordLogin.error = "Fill your password"
            } else {
                val editor = sharedPref.edit()
                editor.putString("email", email)
                editor.apply()

                startActivity(Intent(this, NearestScheduleActivity::class.java))
            }

            val value = sharedPref.getString("email", "")
            Toast.makeText(this, "this $value", Toast.LENGTH_SHORT).show()
        }

        binding.tvRegisterLogin.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}