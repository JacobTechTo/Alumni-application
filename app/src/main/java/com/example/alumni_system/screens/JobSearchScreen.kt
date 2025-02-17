package com.example.alumni_system.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun JobSearchScreen(navController: NavHostController, uid: String) {
    var jobList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    // Fetch jobs from Firestore
    LaunchedEffect(Unit) {
        db.collection("jobs").addSnapshotListener { snapshot, _ ->
            snapshot?.let {
                jobList = it.documents.mapNotNull { doc -> doc.data }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Jobs") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            val filteredJobs = jobList.filter { job ->
                job["title"].toString().contains(searchQuery, ignoreCase = true) ||
                        job["category"].toString().contains(searchQuery, ignoreCase = true) ||
                        job["jobType"].toString().contains(searchQuery, ignoreCase = true)
            }

            items(filteredJobs) { job ->
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = job["title"].toString(), style = MaterialTheme.typography.headlineSmall)
                        Text(text = job["category"].toString(), style = MaterialTheme.typography.bodyMedium)
                        Text(text = job["jobType"].toString(), style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Salary: ${job["salaryRange"]}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Status: ${job["status"]}", style = MaterialTheme.typography.bodyMedium)

                        // Apply Now button**
                        Button(
                            onClick = { applyForJob(uid, job["id"].toString(), context, db) },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Apply Now")
                        }
                    }
                }
            }
        }
    }
}

// Function to apply for a job and check if alumni has already applied
fun applyForJob(uid: String, jobId: String, context: Context, db: FirebaseFirestore) {
    val applicationsRef = db.collection("applications")

    // Check if the alumni has already applied
    applicationsRef.whereEqualTo("uid", uid)
        .whereEqualTo("jobId", jobId)
        .get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                Toast.makeText(context, "You have already applied for this job!", Toast.LENGTH_SHORT).show()
            } else {
                // Add application to Firestore
                val application = hashMapOf(
                    "jobId" to jobId,
                    "uid" to uid,
                    "status" to "Pending"
                )

                applicationsRef.add(application)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Application submitted!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to apply.", Toast.LENGTH_SHORT).show()
                    }
            }
        }
}
