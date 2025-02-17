package com.example.alumni_system.utils

import android.annotation.SuppressLint
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreService {
    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()

    fun addJobApplication(uid: String, jobId: String) {
        val application = hashMapOf(
            "uid" to uid,
            "jobId" to jobId,
            "status" to "Pending",
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("applications").add(application)
    }
}
