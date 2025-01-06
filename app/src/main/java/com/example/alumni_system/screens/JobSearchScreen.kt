package com.example.alumni_system.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun JobSearchScreen(navController: NavHostController) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    // State to hold the list of job postings
    val jobList = remember { mutableStateListOf<Map<String, Any>>() }

    // Fetch job postings from Firestore
    LaunchedEffect(Unit) {
        db.collection("jobs").get()
            .addOnSuccessListener { result ->
                jobList.clear()
                for (document in result) {
                    jobList.add(document.data)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // UI for the job search screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Job Search and Applications",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(jobList) { job ->
                JobCard(
                    job = job,
                    onApplyClick = {
                        // Add application to Firestore
                        db.collection("applications").add(
                            mapOf(
                                "jobTitle" to job["title"],
                                "timestamp" to System.currentTimeMillis()
                            )
                        ).addOnSuccessListener {
                            Toast.makeText(context, "Applied successfully!", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener { exception ->
                            Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun JobCard(
    job: Map<String, Any>,
    onApplyClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = job["title"].toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = job["description"].toString(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Button(
                onClick = onApplyClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Apply")
            }
        }
    }
}

