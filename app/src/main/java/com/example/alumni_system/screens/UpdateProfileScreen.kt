package com.example.alumni_system.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun UpdateProfileScreen(navController: NavHostController, userId: String) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    // State variables to store profile details
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("") }
    var workExperience by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var personalProjects by remember { mutableStateOf("") }

    // Fetch user profile details
    LaunchedEffect(Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    name = document.getString("name").orEmpty()
                    email = document.getString("email").orEmpty()
                    education = document.getString("education").orEmpty()
                    workExperience = document.getString("workExperience").orEmpty()
                    skills = document.getString("skills").orEmpty()
                    personalProjects = document.getString("personalProjects").orEmpty()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Update Profile",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Input fields for profile details
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = education,
            onValueChange = { education = it },
            label = { Text("Education") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = workExperience,
            onValueChange = { workExperience = it },
            label = { Text("Work Experience") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = skills,
            onValueChange = { skills = it },
            label = { Text("Skills") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = personalProjects,
            onValueChange = { personalProjects = it },
            label = { Text("Personal Projects") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                // Update profile in Firestore
                val updatedProfile = mapOf(
                    "name" to name,
                    "email" to email,
                    "education" to education,
                    "workExperience" to workExperience,
                    "skills" to skills,
                    "personalProjects" to personalProjects
                )

                db.collection("users").document(userId).set(updatedProfile)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }
    }
}
