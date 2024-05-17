package com.example.digitrack.activities

// MaterialsActivity.kt
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaterialsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvMaterials = binding.rvMaterials
        rvLevels = binding.rvLevels

        rvMaterials.layoutManager = LinearLayoutManager(this)
        rvLevels.layoutManager = LinearLayoutManager(this)

        loadMaterials()
        loadLevels()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadMaterials() {
        val db = FirebaseFirestore.getInstance()
        db.collection("materials").get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot) {
                val material = document.toObject(Materials::class.java)
                materialsList.add(material)
            }
            rvMaterials.adapter = MaterialsAdapter(materialsList) { position ->
                // Handle click event for materials
                Toast.makeText(this, "Material clicked at position $position", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to load materials: $exception", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadLevels() {
        val db = FirebaseFirestore.getInstance()
        db.collection("levels").get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot) {
                val level = document.toObject(Levels::class.java)
                levelsList.add(level)
            }
            rvLevels.adapter = LevelsAdapter(levelsList) { position ->
                // Handle click event for levels
                Toast.makeText(this, "Level clicked at position $position", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to load levels: $exception", Toast.LENGTH_SHORT).show()
        }
    }
}