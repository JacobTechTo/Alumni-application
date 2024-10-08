package com.example.alumni_world.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.alumni_world.nav.Screens
import com.google.firebase.auth.FirebaseAuth

@Composable
fun EmailVerificationScreen(navController: NavHostController) {
    // Initialize Firebase Auth
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Get the current context for displaying Toast
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Please verify your email before accessing the dashboard.")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Check if the current user is logged in and attempt to reload their data
                val user = auth.currentUser
                user?.reload()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // If email is verified, navigate to the dashboard
                        if (user.isEmailVerified) {
                            Toast.makeText(context, "Email verified. Accessing Dashboard.", Toast.LENGTH_SHORT).show()
                            navController.navigate(Screens.DashboardScreen.route)
                        } else {
                            Toast.makeText(context, "Email not verified yet.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // If there's an error while reloading, show an error message
                        Toast.makeText(context, "Failed to reload user. Try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        ) {
            Text("Check Verification Status")
        }
    }
}