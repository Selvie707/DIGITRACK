package com.example.digitrack.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.digitrack.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var email: String
    private lateinit var password: String
    private var db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private val TAG = LoginActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val sharedPref = applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val usersCollection = db.collection("teacher")

        binding.btnLoginOnboarding.setOnClickListener {
            email = binding.etEmailLogin.text.toString()
            password = binding.etPasswordLogin.text.toString()

            if (email.isBlank() || !email.contains("@") || !email.contains(".")) {
                binding.etEmailLogin.error = "Fill proper email"
            } else if (password.isBlank()) {
                binding.etPasswordLogin.error = "Fill your password"
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser


                        val query = usersCollection.whereEqualTo("userEmail", email)
                        query.get()
                            .addOnSuccessListener { result ->
                                if (result.isEmpty) {
                                    binding.etEmailLogin.error = "Email not found"
                                } else {
                                    for (document in result) {
                                        val userData = document.data

                                        val id = document.getString("userId")
                                        val name = document.getString("userName")
                                        val email = document.getString("userEmail")
                                        val role = document.getString("userRole")

                                        Log.d("Firestore", "$id: $email and $name")

                                        val editor = sharedPref.edit()
                                        editor.putString("id", id)
                                        editor.putString("name", name)
                                        editor.putString("email", email)
                                        editor.putString("role", role)
                                        editor.apply()

                                        startActivity(Intent(this, NearestScheduleActivity::class.java))
                                        finish()
                                    }
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.w("Firestore", "Error getting documents: ", exception)
                                Toast.makeText(this, "There's something wrong", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }


//                val query = usersCollection.whereEqualTo("email", email)
//                query.get()
//                    .addOnSuccessListener { result ->
//                        if (result.isEmpty) {
//                            binding.etEmailLogin.error = "Email not found"
//                        } else {
//                            for (document in result) {
//                                val id = document.getString("id")
//                                val name = document.getString("name")
//                                val email = document.getString("email")
//                                val aPassword = document.getString("password")
//
//                                Log.d("Firestore", "$id: $email and $name")
//
//                                if (aPassword == password) {
//                                    val editor = sharedPref.edit()
//                                    editor.putString("id", id)
//                                    editor.putString("name", name)
//                                    editor.apply()
//
//                                    startActivity(Intent(this, NearestScheduleActivity::class.java))
//                                    finish()
//                                } else {
//                                    binding.etPasswordLogin.error = "Password not match"
//                                }
//                            }
//                        }
//                    }
//                    .addOnFailureListener { exception ->
//                        Log.w("Firestore", "Error getting documents: ", exception)
//                        Toast.makeText(this, "There's something wrong", Toast.LENGTH_SHORT).show()
//                    }
                }
            }
        }

        binding.tvRegisterLogin.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}