package com.example.alumni_system.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.alumni_system.R
import com.example.alumni_system.nav.Screens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

// Initialize Firebase Auth globally
// FirebaseAuth instance
private lateinit var auth: FirebaseAuth

@Composable
fun LoginScreen(navController: NavHostController) {
    // Initialize Firebase Auth
    auth = FirebaseAuth.getInstance()

    // State variables for user input and feedback
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("alumni") } // Default role as "alumni"
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create an Account",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "Login",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email input field
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password input field
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Role Picker (Admin/Alumni)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { role = "admin" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (role == "admin") MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    ) {
                        Text("Admin")
                    }
                    Button(
                        onClick = { role = "alumni" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (role == "alumni") MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    ) {
                        Text("Alumni")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sign Up Button
                Button(
                    onClick = {
                        signUp(
                            email = email,
                            password = password,
                            role = role,
                            onSuccess = {
                                successMessage = "Verification email sent! Check your inbox."
                                navController.navigate(Screens.EmailVerificationScreen.route)
                            },
                            onFailure = { error ->
                                errorMessage = error
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign Up")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Login Button for Existing Users
                TextButton(
                    onClick = {
                        navController.navigate(Screens.SignInScreen.route)
                    }
                ) {
                    Text(
                        text = "Already have an account? Log in here",
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Display error message if sign-up fails
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Display success message
                successMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// Function to handle user registration and store role in Firestore
private fun signUp(
    email: String,
    password: String,
    role: String,
    onSuccess: (FirebaseUser?) -> Unit,
    onFailure: (String) -> Unit
) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val db = FirebaseFirestore.getInstance()

                // Save the user's role in Firestore
                val userRole = hashMapOf(
                    "uid" to (user?.uid ?: ""),
                    "role" to role,
                    "email" to email
                )

                db.collection("users").document(user?.uid ?: "")
                    .set(userRole)
                    .addOnSuccessListener {
                        // Send email verification
                        user?.sendEmailVerification()
                            ?.addOnCompleteListener { verificationTask ->
                                if (verificationTask.isSuccessful) {
                                    onSuccess(user)
                                } else {
                                    onFailure(verificationTask.exception?.message ?: "Failed to send verification email.")
                                }
                            }
                    }
                    .addOnFailureListener {
                        onFailure("Failed to store user role in Firestore: ${it.message}")
                    }
            } else {
                onFailure(task.exception?.message ?: "Sign-up failed.")
            }
        }
}
