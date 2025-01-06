package com.example.alumni_system.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.alumni_system.nav.Screens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun EmailVerificationScreen(
    navController: NavHostController,
    auth: FirebaseAuth = FirebaseAuth.getInstance() // Use default FirebaseAuth instance
) {
    // Use LocalContext for displaying Toast messages
    val context = LocalContext.current

    // Firebase User instance
    val currentUser = auth.currentUser

    // State variables for success and error messages
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Instructional Text
        Text(text = "Please verify your email before accessing the dashboard.")
        Spacer(modifier = Modifier.height(16.dp))

        // Check Verification Status Button
        Button(onClick = {
            currentUser?.reload()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (currentUser.isEmailVerified) {
                        // Navigate to appropriate screen based on role
                        fetchUserRoleAndNavigate(currentUser.uid, navController, context)
                    } else {
                        // Display Toast for unverified email
                        successMessage = null
                        errorMessage = "Email not verified yet."
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle errors during reload
                    successMessage = null
                    errorMessage =
                        "Failed to check email verification status: ${task.exception?.message}"
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                // No authenticated user found
                successMessage = null
                errorMessage = "No authenticated user found. Please log in."
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Check Verification Status")
        }

        // Display Success Message
        successMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = androidx.compose.ui.graphics.Color.Green)
        }

        // Display Error Message
        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = androidx.compose.ui.graphics.Color.Red)
        }
    }
}

/**
 * Fetches the user's role from Firestore and navigates to the appropriate screen.
 */
fun fetchUserRoleAndNavigate(
    userId: String,
    navController: NavHostController,
    context: android.content.Context
) {
    val db = FirebaseFirestore.getInstance()

    db.collection("users").document(userId).get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val role = document.getString("role")

                // Navigate based on the user's role
                when (role) {
                    "superuser" -> {
                        navController.navigate(Screens.SuperUserScreen.route) {
                            popUpTo(0)
                        }
                    }

                    "admin" -> {
                        Toast.makeText(context, "Welcome Admin!", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screens.AdminScreen.route) {
                            popUpTo(0) // Clear backstack
                        }
                    }
                    "alumni" -> {
                        Toast.makeText(context, "Welcome Alumni!", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screens.AlumniScreen.route) {
                            popUpTo(0) // Clear backstack
                        }
                    }
                    else -> {
                        Toast.makeText(context, "Invalid role. Please contact support.", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screens.LoginScreen.route) {
                            popUpTo(0)
                        }
                    }
                }
            } else {
                Toast.makeText(context, "User document not found.", Toast.LENGTH_SHORT).show()
                navController.navigate(Screens.LoginScreen.route) {
                    popUpTo(0)
                }
            }
        }
        .addOnFailureListener { exception ->
            Toast.makeText(
                context,
                "Failed to fetch role: ${exception.message}",
                Toast.LENGTH_SHORT
            ).show()
            navController.navigate(Screens.LoginScreen.route) {
                popUpTo(0)
            }
        }
}
