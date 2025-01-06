package com.example.alumni_system.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast

@Composable
fun ManageRolesScreen(navController: NavHostController) {
    val users = remember { mutableStateListOf<Map<String, Any>>() }
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        db.collection("users").get().addOnSuccessListener { result ->
            for (document in result) {
                users.add(document.data + ("id" to document.id))
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Manage Roles", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        users.forEach { user ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${user["email"]} - ${user["role"]}")
                Button(onClick = {
                    db.collection("users").document(user["id"].toString())
                        .update("role", if (user["role"] == "admin") "alumni" else "admin")
                        .addOnSuccessListener {
                            Toast.makeText(context, "Role updated.", Toast.LENGTH_SHORT).show()
                        }
                }) {
                    Text("Toggle Role")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
