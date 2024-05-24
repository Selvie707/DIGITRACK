package com.example.digitrack

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inisialisasi Firestore
        val db = FirebaseFirestore.getInstance()

        // Aktifkan caching
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        db.firestoreSettings = settings
    }
}