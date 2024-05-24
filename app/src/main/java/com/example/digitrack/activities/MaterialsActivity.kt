package com.example.digitrack.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.adapters.MaterialsAdapter
import com.example.digitrack.adapters.LevelsAdapter
import com.example.digitrack.data.Levels
import com.example.digitrack.data.Materials
import com.example.digitrack.databinding.ActivityMaterialsBinding
import com.google.firebase.firestore.FirebaseFirestore

class MaterialsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMaterialsBinding
    private lateinit var rvMaterials: RecyclerView
    private lateinit var rvLevels: RecyclerView

    private val materialsList = mutableListOf<Materials>()
    private val levelsList = mutableListOf<Levels>()
    private lateinit var materialsAdapter: MaterialsAdapter
    private var selectedLevelId: String = "J1"
    private var selectedCurName: String = "DK3"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaterialsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvMaterials = binding.rvMaterials
        rvLevels = binding.rvLevels

        rvMaterials.layoutManager = LinearLayoutManager(this)
        rvLevels.layoutManager = LinearLayoutManager(this)

        binding.btnDK2.setOnClickListener {
            selectedCurName = binding.btnDK2.text.toString()
            Log.d("Cek ID Level8", "$selectedCurName - $selectedLevelId")
            loadMaterials(selectedCurName+selectedLevelId)
        }

        binding.btnDK3.setOnClickListener {
            selectedCurName = binding.btnDK3.text.toString()
            Log.d("Cek ID Level3", "$selectedCurName - $selectedLevelId")
            loadMaterials(selectedCurName+selectedLevelId)
        }

        binding.btnACD.setOnClickListener {
            selectedCurName = binding.btnACD.text.toString()
            Log.d("Cek ID Level2", "$selectedCurName - $selectedLevelId")
            loadMaterials(selectedCurName+selectedLevelId)
        }

        materialsAdapter = MaterialsAdapter(materialsList) { position ->
            // Handle click event for materials
            Toast.makeText(this, "Material clicked at position $position", Toast.LENGTH_SHORT).show()
        }
        rvMaterials.adapter = materialsAdapter

        loadLevels()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadMaterials(levelId: String) {
        val db = FirebaseFirestore.getInstance()
        Log.d("Cek ID Level7", levelId)
        db.collection("materials")
            .whereEqualTo("levelId", levelId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                materialsList.clear()
                for (document in querySnapshot) {
                    val material = document.toObject(Materials::class.java)
                    materialsList.add(material)
                }
                materialsAdapter.notifyDataSetChanged()
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load materials: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadLevels() {
        val db = FirebaseFirestore.getInstance()
        db.collection("levels")
            .orderBy("levelName").get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot) {
                val level = document.toObject(Levels::class.java)
                levelsList.add(level)
            }
                Log.d("Cek ID Level1", "$selectedCurName - $selectedLevelId")
                loadMaterials(selectedCurName+selectedLevelId)
            rvLevels.adapter = LevelsAdapter(levelsList) { position ->
                val levelId = levelsList[position].levelId
                Log.d("Cek ID Level6", levelId)
                selectedLevelId = levelId
                Log.d("Cek ID Level", levelId)
                loadMaterials(levelId)
                Toast.makeText(this, "Level clicked at position $position", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to load levels: $exception", Toast.LENGTH_SHORT).show()
        }
    }
}