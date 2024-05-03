package com.example.digitrack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.digitrack.databinding.ActivityRegisterBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var repPassword: String
    private lateinit var role: String
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spinnerList = listOf("Role", "Admin", "Teacher")

        val arrayAdapter:ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_activated_1, spinnerList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spRole.adapter = arrayAdapter

        val usersCollection = db.collection("teacher")

        binding.btnLoginOnboarding.setOnClickListener {
            name = binding.etName.text.toString().trim()
            email = binding.etEmailRegister.text.toString().trim()
            password = binding.etPasswordRegister.text.toString().trim()
            repPassword = binding.etRepeatPassword.text.toString().trim()
            role = binding.spRole.selectedItem.toString().trim()

            if (name.isBlank()) {
                binding.etName.error = "Please fill up this field"
            } else if (email.isBlank()) {
                binding.etEmailRegister.error = "Please fill up this field"
            } else if (email.isBlank() || !email.contains("@") || !email.contains(".")) {
                binding.etEmailRegister.error = "Fill proper email"
            } else if (password.isBlank()) {
                binding.etPasswordRegister.error = "Please fill up this field"
            } else if (repPassword.isBlank()) {
                binding.etRepeatPassword.error = "Please fill up this field"
            } else if (password != repPassword) {
                binding.etPasswordRegister.error = "Password not match"
                binding.etRepeatPassword.error = "Password not match"
            } else if (role.isBlank()) {
                binding.etName.error = "Please fill up this field"
            } else {
                usersCollection.get()
                    .addOnSuccessListener { result ->
                        // Menghitung jumlah dokumen yang ada
                        val count = result.size()

                        // Menghasilkan ID baru dengan format yang diinginkan
                        val newID = "USR${String.format("%03d", count + 1)}"

                        val userMap = hashMapOf(
                            "id" to newID,
                            "name" to name,
                            "email" to email,
                            "password" to password,
                            "role" to role
                        )

                        usersCollection.document(newID).set(userMap).addOnSuccessListener {
                            Toast.makeText(this, "Successfully Added!!!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "There's something wrong", Toast.LENGTH_SHORT).show()
                                Log.d("RegisterActivity", "Error: ${e.message}")
                            }
                    }
            }

            usersCollection
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        // Mendapatkan data dari setiap dokumen
                        val name = document.getString("name")
                        val email = document.getString("email")
                        // Lakukan sesuatu dengan data yang didapatkan
                        Log.d("Firestore", "$name: $email")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("Firestore", "Error getting documents: ", exception)
                }

//            db.collection("student").document("abc").set(userMap)
//                .addOnSuccessListener {
//
//                }
//                .addOnFailureListener {
//                    Toast.makeText(this, "Failed!!!", Toast.LENGTH_SHORT).show()
//                    Log.d("bbb", "bbb")
//                }
//
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
        }

        binding.tvLoginRegister.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}