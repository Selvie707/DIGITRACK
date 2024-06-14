package com.example.digitrack.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.digitrack.databinding.ActivityRegisterBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var repPassword: String
    private lateinit var role: String
    private var db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

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
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information

                        val user = auth.currentUser

                        val userId = user?.uid

                                // Menghitung jumlah dokumen yang ada
                                //val count = result.size()

                                // Menghasilkan ID baru dengan format yang diinginkan
                                //val newID = "USR${String.format("%03d", count + 1)}"

                                val userMap = hashMapOf(
                                    "userId" to userId,
                                    "userName" to name,
                                    "userEmail" to email,
                                    "userRole" to role
                                )

                                usersCollection.document(userId!!).set(userMap).addOnSuccessListener {
                                    Toast.makeText(this, "Successfully Added!!!", Toast.LENGTH_SHORT).show()

                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "There's something wrong", Toast.LENGTH_SHORT).show()
                                        Log.d("RegisterActivity", "Error: ${e.message}")
                                    }

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            }
        }

        binding.tvLoginRegister.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}