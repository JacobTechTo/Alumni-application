package com.example.alumni_system.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

object NotificationManager {
    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()

    fun sendNotification(uid: String, email: String, message: String, context: Context) {
        val notification = hashMapOf(
            "userId" to uid,
            "userEmail" to email,
            "message" to message,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("notifications").add(notification)
            .addOnSuccessListener {
                Toast.makeText(context, "Notification sent!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to send notification.", Toast.LENGTH_SHORT).show()
            }
    }
}
