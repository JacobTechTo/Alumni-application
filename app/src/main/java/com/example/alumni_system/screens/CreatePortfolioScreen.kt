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
fun CreatePortfolioScreen(navController: NavHostController, email: String? = null) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf(email ?: "") }
    var education by remember { mutableStateOf("") }
    var workExperience by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var projects by remember { mutableStateOf("") }

    // Load existing portfolio if email is provided
    LaunchedEffect(userEmail) {
        if (!userEmail.isNullOrEmpty()) {
            db.collection("portfolios").document(userEmail).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        name = document.getString("name").orEmpty()
                        education = document.getString("education").orEmpty()
                        workExperience = document.getString("workExperience").orEmpty()
                        skills = document.get("skills")?.let { (it as List<*>).joinToString(", ") }.orEmpty()
                        projects = document.get("projects")?.let { (it as List<*>).joinToString(", ") }.orEmpty()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Failed to load portfolio: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            "Create / Update Portfolio",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = userEmail,
            onValueChange = { userEmail = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNullOrEmpty() // Disable if email is provided
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = education,
            onValueChange = { education = it },
            label = { Text("Education") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = workExperience,
            onValueChange = { workExperience = it },
            label = { Text("Work Experience") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = skills,
            onValueChange = { skills = it },
            label = { Text("Skills (comma-separated)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = projects,
            onValueChange = { projects = it },
            label = { Text("Personal Projects") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (userEmail.isBlank()) {
                    Toast.makeText(context, "Email cannot be empty", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val portfolio = mapOf(
                    "name" to name,
                    "email" to userEmail,
                    "education" to education,
                    "workExperience" to workExperience,
                    "skills" to skills.split(",").map { it.trim() },
                    "projects" to projects.split(",").map { it.trim() }
                )

                db.collection("portfolios").document(userEmail).set(portfolio)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Portfolio updated successfully!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "Failed to update portfolio: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Save")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("update_profile_screen/$userEmail") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Edit Profile")
        }
    }
}




