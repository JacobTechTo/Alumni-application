package com.example.alumni_system.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun AlumniDirectoryScreen(navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    var alumniList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var filteredList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    // Fetch alumni data from Firestore

    LaunchedEffect(Unit) {
        db.collection("portfolios").get()
            .addOnSuccessListener { documents ->
                val data = documents.map { it.data }
                alumniList = data
                filteredList = data
            }
            .addOnFailureListener {
                alumniList = emptyList()
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Alumni Directory", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                filteredList = if (searchQuery.isBlank()) {
                    alumniList
                } else {
                    alumniList.filter { alumnus ->
                        val name = alumnus["name"]?.toString()?.lowercase().orEmpty()
                        val email = alumnus["email"]?.toString()?.lowercase().orEmpty()
                        searchQuery.lowercase() in name || searchQuery.lowercase() in email
                    }
                }
            },
            label = { Text("Search by name or email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Alumni list
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredList.size) { index ->
                val alumnus = filteredList[index]
                AlumniCard(
                    alumnus = alumnus,
                    onClick = {
                        val email = alumnus["email"]?.toString().orEmpty()
                        if (email.isNotBlank()) {
                            navController.navigate("view_profile_screen/$email")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun AlumniCard(alumnus: Map<String, Any>, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = alumnus["name"]?.toString().orEmpty(), style = MaterialTheme.typography.bodyLarge)
            Text(text = alumnus["email"]?.toString().orEmpty(), style = MaterialTheme.typography.bodySmall)
            Text(
                text = alumnus["workExperience"]?.toString().orEmpty(),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
