package com.example.alumni_system.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar

@Composable
fun AnalyticsScreen(navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()

    // State variables for analytics
    var totalApplications by remember { mutableIntStateOf(0) }
    var rejectedApplications by remember { mutableIntStateOf(0) }
    var successfulHires by remember { mutableIntStateOf(0) }
    var activeJobs by remember { mutableIntStateOf(0) }
    var closedJobs by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var applicationsList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val applicationsSnapshot = db.collection("applications").get().await()
                val jobsSnapshot = db.collection("jobs").get().await()

                // Processing application data
                totalApplications = applicationsSnapshot.size()
                rejectedApplications = applicationsSnapshot.count {
                    it.getString("status") == "Rejected"
                }
                successfulHires = applicationsSnapshot.count {
                    it.get("status") == "Hired"
                }

                applicationsList = applicationsSnapshot.documents.mapNotNull { it.data }

                // Processing job data
                activeJobs = jobsSnapshot.count { it.getString("status") == "Active" }
                closedJobs = jobsSnapshot.count { it.getString("status") == "Closed" }

                isLoading = false
            } catch (e: Exception) {
                errorMessage = "Error loading analytics: ${e.message}"
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Job Analytics", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage != null) {
            Text(errorMessage ?: "Unknown error", color = MaterialTheme.colorScheme.error)
        } else {
            AnalyticsCard(title = "Total Applications", value = totalApplications.toString())
            AnalyticsCard(title = "Rejected Applications", value = rejectedApplications.toString())
            AnalyticsCard(title = "Successful Hires", value = successfulHires.toString())
            AnalyticsCard(title = "Active Job Postings", value = activeJobs.toString())
            AnalyticsCard(title = "Closed Job Postings", value = closedJobs.toString())

            Spacer(modifier = Modifier.height(16.dp))

            Text("Pending Applications", style = MaterialTheme.typography.titleMedium)

            applicationsList.forEach { application ->
                val uid = application["uid"] as? String ?: ""
                val jobId = application["jobId"] as? String ?: ""
                val applicationId = application["applicationId"] as? String ?: ""

                ApplicationItem(uid, jobId, applicationId, db)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Back to Admin Panel")
        }
    }
}

@Composable
fun ApplicationItem(uid: String, jobId: String, applicationId: String, db: FirebaseFirestore) {
    var alumniProfile by remember { mutableStateOf<Map<String, Any>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val alumniSnapshot = db.collection("users").document(uid).get().await()
                alumniProfile = alumniSnapshot.data
                isLoading = false
            } catch (e: Exception) {
                errorMessage = "Error loading profile: ${e.message}"
                isLoading = false
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Alumni ID: $uid", style = MaterialTheme.typography.bodyLarge)
            Text("Job ID: $jobId", style = MaterialTheme.typography.bodyLarge)

            if (isLoading) {
                CircularProgressIndicator()
            } else if (errorMessage != null) {
                Text(errorMessage ?: "Unknown error", color = MaterialTheme.colorScheme.error)
            } else {
                Text("Alumni Qualifications: ${alumniProfile?.get("qualifications") ?: "Not available"}")
                Text("Experience: ${alumniProfile?.get("experience") ?: "Not available"}")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        updateApplicationStatus(applicationId, "Hired", uid, db)
                    }) {
                        Text("Accept")
                    }

                    Button(onClick = {
                        updateApplicationStatus(applicationId, "Rejected", uid, db)
                    }) {
                        Text("Reject")
                    }

                    Button(onClick = { manuallyCloseJob(jobId, db) }) {
                        Text("Close Job")
                    }
                }
            }
        }
    }
}

fun updateApplicationStatus(applicationId: String, status: String, uid: String, db: FirebaseFirestore) {
    val applicationRef = db.collection("applications").document(applicationId)

    applicationRef.update("status", status)
        .addOnSuccessListener {
            sendNotification(uid, "Your application has been $status.")
        }
        .addOnFailureListener { e ->
            println("Error updating application: ${e.message}")
        }
}

fun manuallyCloseJob(jobId: String, db: FirebaseFirestore) {
    val jobRef = db.collection("jobs").document(jobId)

    jobRef.update("status", "Closed")
        .addOnSuccessListener { println("Job closed successfully") }
        .addOnFailureListener { e -> println("Error closing job: ${e.message}") }
}

fun sendNotification(uid: String, message: String) {
    val db = FirebaseFirestore.getInstance()
    val notification = mapOf(
        "userId" to uid,
        "message" to message,
        "timestamp" to Calendar.getInstance().time
    )

    db.collection("notifications").add(notification)
        .addOnSuccessListener {
            println("Notification sent successfully")
        }
        .addOnFailureListener { e ->
            println("Error sending notification: ${e.message}")
        }
}

@Composable
fun AnalyticsCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(text = value, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}
