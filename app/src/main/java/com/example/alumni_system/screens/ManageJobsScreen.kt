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
import com.example.alumni_system.nav.Screens
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ManageJobsScreen(navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    // State to hold job postings
    var jobs by remember { mutableStateOf(listOf<Map<String, Any>>()) }

    // Fetch jobs from Firestore
    LaunchedEffect(Unit) {
        db.collection("jobs").get().addOnSuccessListener { result ->
            jobs = result.documents.map { it.data?.plus("id" to it.id) ?: emptyMap() }
        }.addOnFailureListener { exception ->
            Toast.makeText(context, "Failed to load jobs: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Manage Jobs", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Display job postings in a list
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(jobs) { job ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Title: ${job["title"]}", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Description: ${job["description"]}")
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Edit Job Button
                            Button(onClick = {
                                navController.navigate(Screens.EditJobScreen.editJobScreenRoute(job["id"].toString()))
                            }) {
                                Text("Edit")
                            }

                            // Delete Job Button
                            Button(onClick = {
                                db.collection("jobs").document(job["id"].toString())
                                    .delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Job deleted successfully!", Toast.LENGTH_SHORT).show()
                                        jobs = jobs.filterNot { it["id"] == job["id"] }
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(context, "Failed to delete job: ${exception.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}
