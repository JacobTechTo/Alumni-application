package com.example.alumni_system.screens


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun EditJobScreen(navController: NavHostController, jobId: String) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Fetch job details when screen is opened
    LaunchedEffect(jobId) {
        db.collection("jobs").document(jobId).get().addOnSuccessListener { document ->
            title = document.getString("title") ?: ""
            description = document.getString("description") ?: ""
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to fetch job details.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Job Title") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Job Description") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            db.collection("jobs").document(jobId)
                .update(mapOf("title" to title, "description" to description))
                .addOnSuccessListener {
                    Toast.makeText(context, "Job updated successfully!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Failed to update job: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }) {
            Text("Save Changes")
        }
    }
}
