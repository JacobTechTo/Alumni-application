package com.example.alumni_system.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment


@Composable

fun PublishProjectsScreen(navController: NavHostController) {

    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Publish a Project", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Project Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Project Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = link,
            onValueChange = { link = it },
            label = { Text("Project Link (e.g., GitHub, Demo)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isNotEmpty() && description.isNotEmpty() && link.isNotEmpty()) {
                    val project = mapOf(
                        "title" to title,
                        "description" to description,
                        "link" to link,
                        "timestamp" to System.currentTimeMillis()
                    )
                    db.collection("projects").add(project)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Project published successfully!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "All fields are required.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Publish Project")
        }
    }
}
