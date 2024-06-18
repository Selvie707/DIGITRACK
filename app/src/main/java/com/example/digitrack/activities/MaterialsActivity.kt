package com.example.digitrack.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.digitrack.R
import com.example.digitrack.adapters.MaterialsAdapter
import com.example.digitrack.adapters.LevelsAdapter
import com.example.digitrack.data.Levels
import com.example.digitrack.data.Materials
import com.example.digitrack.databinding.ActivityMaterialsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MaterialsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMaterialsBinding
    private lateinit var rvMaterials: RecyclerView
    private lateinit var rvLevels: RecyclerView

    private val materialsList = mutableListOf<Materials>()
    private val levelsList = mutableListOf<Levels>()
    private lateinit var materialsAdapter: MaterialsAdapter
    private var selectedLevelId: String = "K1"
    private var selectedCurName: String = "DK2"

//    private val intentToAddActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback { result: ActivityResult ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            // Hasil sukses, lakukan sesuatu dengan data dari intent
//            val data: Intent? = result.data
//            // Misalnya, ambil data dari intent
//            val someData = data?.getStringExtra("key")
//            // Lakukan sesuatu dengan data yang diambil
//            if (someData != null) {
//                loadMaterials(someData)
//            }
//            Toast.makeText(this, "Data: $someData", Toast.LENGTH_SHORT).show()
//        } else {
//            // Hasil gagal, mungkin tampilkan pesan atau lakukan hal lain
//            Toast.makeText(this, "Action canceled or failed", Toast.LENGTH_SHORT).show()
//        }})
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaterialsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val role = sharedPref.getString("role", "")

        if (!role.equals("Admin")) {
            binding.btnAdd.visibility = View.GONE
        }

        rvMaterials = binding.rvMaterials
        rvLevels = binding.rvLevels

        rvMaterials.layoutManager = LinearLayoutManager(this)
        rvLevels.layoutManager = LinearLayoutManager(this)

        val purpleColor = ContextCompat.getColor(this, R.color.purple)

        binding.btnDK2.setOnClickListener {
            binding.btnDK2.setBackgroundResource(R.drawable.bg_corner_backii)
            binding.btnDK2.setTextColor(Color.WHITE)
            binding.btnDK3.setBackgroundResource(R.drawable.bg_corner_back)
            binding.btnDK3.setTextColor(purpleColor)
            binding.btnACD.setBackgroundResource(R.drawable.bg_corner_back)
            binding.btnACD.setTextColor(purpleColor)

            selectedCurName = binding.btnDK2.text.toString()
            selectedLevelId = "K1"
            loadLevels(selectedCurName)
            loadMaterials(selectedCurName+selectedLevelId)
        }

        binding.btnDK3.setOnClickListener {
            binding.btnDK3.setBackgroundResource(R.drawable.bg_corner_backii)
            binding.btnDK3.setTextColor(Color.WHITE)
            binding.btnDK2.setBackgroundResource(R.drawable.bg_corner_back)
            binding.btnDK2.setTextColor(purpleColor)
            binding.btnACD.setBackgroundResource(R.drawable.bg_corner_back)
            binding.btnACD.setTextColor(purpleColor)

            selectedCurName = binding.btnDK3.text.toString()
            selectedLevelId = "LC1L1"
            loadLevels(selectedCurName)
            loadMaterials(selectedCurName+selectedLevelId)
        }

        binding.btnACD.setOnClickListener {
            binding.btnACD.setBackgroundResource(R.drawable.bg_corner_backii)
            binding.btnACD.setTextColor(Color.WHITE)
            binding.btnDK3.setBackgroundResource(R.drawable.bg_corner_back)
            binding.btnDK3.setTextColor(purpleColor)
            binding.btnDK2.setBackgroundResource(R.drawable.bg_corner_back)
            binding.btnDK2.setTextColor(purpleColor)

            selectedCurName = binding.btnACD.text.toString()
            selectedLevelId = "J"
            loadLevels(selectedCurName)
            loadMaterials(selectedCurName+selectedLevelId)
        }

        materialsAdapter = MaterialsAdapter(materialsList) {
            // Handle click event for materials
        }
        rvMaterials.adapter = materialsAdapter

        loadLevels(selectedCurName)

        binding.btnAdd.setOnClickListener {
            val cur = "AKD"
//            startActivity(Intent(this, EditProfileActivity::class.java))
            // Membuat intent ke Activity tujuan
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("curriculum", cur)
            startActivity(intent)

//            Meluncurkan Activity dan menunggu hasilnya
//            intentToAddActivity.launch(intent)
//            finish()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadMaterials(levelId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("materials")
            .whereEqualTo("levelId", levelId)
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    Toast.makeText(this, "Failed to load students: $exception", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (querySnapshot != null) {
                    materialsList.clear()
                    for (document in querySnapshot) {
                        val material = document.toObject(Materials::class.java)
                        materialsList.add(material)
                    }
                    // Mengurutkan materialsList berdasarkan materialId secara ascending
                    materialsList.sortBy { it.materialId }
                    materialsAdapter.notifyDataSetChanged()
                }
            }
    }

    private fun loadLevels(curName: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("levels").whereEqualTo("curName", curName).addSnapshotListener { querySnapshot, exception ->
            if (exception != null) {
                Toast.makeText(this, "Failed to load students: $exception", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (querySnapshot != null) {
                levelsList.clear()
                for (document in querySnapshot) {
                    val level = document.toObject(Levels::class.java)
                    levelsList.add(level)
                }

                levelsList.sortBy { it.levelSeq }

                loadMaterials(selectedCurName+selectedLevelId)
                rvLevels.adapter = LevelsAdapter(levelsList) { position ->
                    var levelId = levelsList[position].levelId
                    levelId = levelId.substring(3)
                    loadMaterials(selectedCurName + levelId)
                }
            }
        }
    }
}