package com.example.alumni_system.screens


import com.example.alumni_system.utils.NotificationManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AlumniProfileScreen(navController: NavHostController,uid: String, applicationId: String, email: String) {
    var alumniData by remember { mutableStateOf<Map<String, Any>?>(null) }
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    // Fetch alumni profile
    LaunchedEffect(uid) {
        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    alumniData = document.data
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        alumniData?.let { data ->
            Text(text = "Name: ${data["name"]}")
            Text(text = "Qualifications: ${data["qualifications"]}")

            Button(
                onClick = { updateApplicationStatus(applicationId, "Accepted", uid, email, context) },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Accept for Interview")
            }

            Button(
                onClick = { updateApplicationStatus(applicationId, "Rejected", uid, email, context) },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Reject")
            }
        }
    }
}

// Update application status and notify alumni
fun updateApplicationStatus(applicationId: String, status: String, uid: String, email: String, context: android.content.Context) {
    val db = FirebaseFirestore.getInstance()

    db.collection("applications").document(applicationId)
        .update("status", status)
        .addOnSuccessListener {
            val message = "Your job application has been $status"
            NotificationManager.sendNotification(uid, email, message, context)
        }
}
