package com.example.digitrack.activities

import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.adapters.DailyReportAdapter
import com.example.digitrack.data.Students
import com.example.digitrack.databinding.ActivityDailyReportBinding
import com.google.firebase.firestore.FirebaseFirestore

class DailyReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDailyReportBinding
    private lateinit var rvDailyReport: RecyclerView
    private lateinit var adapter: DailyReportAdapter
    private val dailyReportList = mutableListOf<Students>()
    private val filteredDailyReportList = mutableListOf<Students>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvDailyReport = binding.rvDailyReport
        rvDailyReport.layoutManager = LinearLayoutManager(this)

        adapter = DailyReportAdapter(filteredDailyReportList) { position ->
            Toast.makeText(this, "Student clicked at position $position", Toast.LENGTH_SHORT).show()
        }
        rvDailyReport.adapter = adapter

        loadDailyReport()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return false
            }
        })
    }

    private fun loadDailyReport() {
        val db = FirebaseFirestore.getInstance()
        db.collection("student").get().addOnSuccessListener { querySnapshot ->
            dailyReportList.clear()
            for (document in querySnapshot) {
                val student = document.toObject(Students::class.java)
                if (student != null) {
                    dailyReportList.add(student)
                }
            }
            filter("") // Filter with empty text to show all data initially
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to load students: $exception", Toast.LENGTH_SHORT).show()
        }
    }

    private fun filter(text: String?) {
        val query = text?.lowercase() ?: ""
        filteredDailyReportList.clear()

        if (query.isEmpty()) {
            filteredDailyReportList.addAll(dailyReportList)
        } else {
            for (student in dailyReportList) {
                val studentName = student.studentName.lowercase()
                val studentLevel = student.levelId.lowercase()
                if (studentName.contains(query) || studentLevel.contains(query)) {
                    filteredDailyReportList.add(student)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }
}
