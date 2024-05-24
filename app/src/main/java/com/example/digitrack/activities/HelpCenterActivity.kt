package com.example.digitrack.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.adapters.AttendancesAdapter
import com.example.digitrack.adapters.HelpCenterAdapter
import com.example.digitrack.data.HelpCenter
import com.example.digitrack.data.Students
import com.example.digitrack.databinding.ActivityHelpCenterBinding
import com.google.firebase.firestore.FirebaseFirestore

class HelpCenterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpCenterBinding
    private lateinit var rvHelpCenter: RecyclerView
    private val helpCenterList = mutableListOf<HelpCenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpCenterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvHelpCenter = binding.rvFaq
        rvHelpCenter.layoutManager = LinearLayoutManager(this)

        loadHelpCenter()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadHelpCenter() {
        val db = FirebaseFirestore.getInstance()
        db.collection("helpcenters").get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot) {
                val helpCenter = document.toObject(HelpCenter::class.java)
                if (helpCenter != null) {
                    println("Question: ${helpCenter.hcQuestion}")
                    println("Answer: ${helpCenter.hcAnswer}")
                }
                helpCenterList.add(helpCenter)
            }

            rvHelpCenter.adapter = HelpCenterAdapter(helpCenterList) { position ->
                Toast.makeText(this, "Help Center clicked at position $position", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to load students: $exception", Toast.LENGTH_SHORT).show()
        }
    }
}