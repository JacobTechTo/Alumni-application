package com.example.alumni_system.screens

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import com.example.alumni_system.nav.Screens

@Composable
fun ModerateContentScreen(navController: NavHostController) {
    val posts = remember { mutableStateListOf<Map<String, Any>>() }
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        db.collection("posts").get().addOnSuccessListener { result ->
            for (document in result) {
                posts.add(document.data + ("id" to document.id))
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Moderate Posts", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        posts.forEach { post ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(post["content"].toString())

                Button(onClick = {
                    db.collection("posts").document(post["id"].toString()).update("isApproved", true)
                }) {
                    Text("Approve")
                }

                Button(onClick = {
                    db.collection("posts").document(post["id"].toString()).delete()
                        .addOnSuccessListener {
                            posts.remove(post)
                            Toast.makeText(context, "Post deleted.", Toast.LENGTH_SHORT).show()
                        }
                }) {
                    Text("Delete")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
