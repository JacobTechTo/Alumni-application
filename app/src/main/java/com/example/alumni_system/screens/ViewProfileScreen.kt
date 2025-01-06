package com.example.alumni_system.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ViewProfileScreen(navController: NavHostController, email: String) {
    val db = FirebaseFirestore.getInstance()
    var profile by remember { mutableStateOf<Map<String, Any>?>(null) }

    LaunchedEffect(email) {
        db.collection("portfolios").document(email).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    profile = document.data
                }
            }
            .addOnFailureListener {
                profile = null
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        profile?.let {
            Text(text = it["name"]?.toString().orEmpty(), style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Email: ${it["email"]?.toString().orEmpty()}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Education: ${it["education"]?.toString().orEmpty()}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Work Experience: ${it["workExperience"]?.toString().orEmpty()}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Skills: ${it["skills"]?.let { skills -> (skills as List<*>).joinToString(", ") }.orEmpty()}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Projects: ${it["projects"]?.let { projects -> (projects as List<*>).joinToString(", ") }.orEmpty()}")
        } ?: Text(text = "Loading profile...")
    }
}
