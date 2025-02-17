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
import com.example.alumni_system.nav.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobsScreen(navController: NavHostController) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var jobType by remember { mutableStateOf("") }
    var salaryRange by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Active Hiring") }
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var expanded by remember { mutableStateOf(false) }
    val jobTypes = listOf("Full-Time", "Part-Time", "Internship")

    var statusExpanded by remember { mutableStateOf(false) }
    val statusOptions = listOf("Active Hiring", "Ended", "Fulfilled")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Post a Job", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Job Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Job Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Job Category") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Job Type Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = jobType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Job Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                jobTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            jobType = type
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Hiring Status Dropdown
        ExposedDropdownMenuBox(
            expanded = statusExpanded,
            onExpandedChange = { statusExpanded = it }
        ) {
            OutlinedTextField(
                value = status,
                onValueChange = {},
                readOnly = true,
                label = { Text("Hiring Status") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = statusExpanded,
                onDismissRequest = { statusExpanded = false }
            ) {
                statusOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            status = option
                            statusExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = salaryRange,
            onValueChange = { salaryRange = it },
            label = { Text("Salary Range") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {
            if (title.isEmpty() || description.isEmpty() || category.isEmpty() || jobType.isEmpty() || salaryRange.isEmpty()) {
                Toast.makeText(context, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            } else {
                val job = hashMapOf(
                    "title" to title,
                    "description" to description,
                    "category" to category,
                    "jobType" to jobType,
                    "salaryRange" to salaryRange,
                    "applications" to 0,
                    "rejected" to 0,
                    "status" to status
                )
                db.collection("jobs").add(job).addOnSuccessListener {
                    Toast.makeText(context, "Job posted successfully.", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }.addOnFailureListener {
                    Toast.makeText(context, "Error posting job.", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text("Post Job")
        }
    }
}
