package com.example.digitrack.activities

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.digitrack.databinding.ActivityEditProfileBinding
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val usersCollection = db.collection("materials")

        // Fetch data from Firestore
        db.collection("levels")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val levelNames = mutableListOf<String>()
                for (document in querySnapshot) {
                    val levelName = document.getString("levelId")
                    if (levelName != null) {
                        levelNames.add(levelName)
                    }
                }
                // Set the adapter to Spinner
                val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, levelNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spRoleEditProfile.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load levels: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        binding.btnUpdate.setOnClickListener {
            val levelId = binding.spRoleEditProfile.selectedItem.toString().trim()
            val materialId = binding.etNameEditProfile.text.toString().trim()
            val materialName = binding.etEmailEditProfile.text.toString().trim()

            val userMap = hashMapOf(
                "levelId" to levelId,
                "materialId" to materialId,
                "materialName" to materialName
            )

            usersCollection.document().set(userMap).addOnSuccessListener {
                Toast.makeText(this, "Successfully Added!!!", Toast.LENGTH_SHORT).show()

                finish()
            }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "There's something wrong", Toast.LENGTH_SHORT).show()
                    Log.d("RegisterActivity", "Error: ${e.message}")
                }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}