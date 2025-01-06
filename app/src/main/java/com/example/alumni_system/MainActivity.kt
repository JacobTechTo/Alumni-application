package com.example.alumni_system

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.alumni_system.nav.NavGraph
import com.example.alumni_system.nav.Screens
import com.example.alumni_system.ui.theme.AlumniSystemTheme
import com.example.alumni_system.utils.SharedViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavHostController
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        enableEdgeToEdge()

        setContent {
            AlumniSystemTheme {
                navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Use LaunchedEffect to handle navigation logic
                    LaunchedEffect(auth) {
                        val currentUser = auth.currentUser

                        if (currentUser == null) {
                            // Navigate to Login Screen if user is not logged in
                            navController.navigate(Screens.LoginScreen.route) {
                                popUpTo(0) // Clears backstack
                            }
                        } else {
                            if (currentUser.isEmailVerified) {
                                // Check role after email verification
                                fetchUserRoleAndNavigate(currentUser.uid, navController)
                            } else {
                                // Navigate to Email Verification Screen
                                navController.navigate(Screens.EmailVerificationScreen.route) {
                                    popUpTo(0) // Clears backstack
                                }
                            }
                        }
                    }

                    // Navigation graph for your app
                    NavGraph(navController = navController, sharedViewModel = sharedViewModel)
                }
            }
        }
    }

    /**
     * Fetches the user's role from Firestore and navigates to the appropriate screen.
     */
    private fun fetchUserRoleAndNavigate(userId: String, navController: NavHostController) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role")
                    Log.d("ROLE_DEBUG", "Fetched role: $role")
                    when (role) {
                        "superuser" -> {
                            navController.navigate(Screens.SuperUserScreen.route) {
                                popUpTo(0)
                            }
                        }

                        "admin" -> {
                            // Navigate to Admin Panel
                            navController.navigate(Screens.AdminScreen.route) {
                                popUpTo(0) // Clears backstack
                            }
                        }
                        "alumni" -> {
                            // Navigate to Alumni Panel
                            navController.navigate(Screens.AlumniScreen.route) {
                                popUpTo(0) // Clears backstack
                            }
                        }
                        else -> {
                            // Handle unexpected role or missing data
                            Log.d("ROLE_DEBUG", "Invalid role or missing role. Navigating to Login.")
                            navController.navigate(Screens.LoginScreen.route) {
                                popUpTo(0)
                            }
                        }
                    }
                } else {
                    // Handle case where user document doesn't exist
                    Log.d("ROLE_DEBUG", "User document does not exist in Firestore.")

                    navController.navigate(Screens.LoginScreen.route) {
                        popUpTo(0)
                    }
                }
            }
            .addOnFailureListener {
                // Handle Firestore failure (e.g., network issues)

                navController.navigate(Screens.LoginScreen.route) {
                    popUpTo(0)
                }
            }
    }
}


//    @Preview(showBackground = true)

//    @Composable
//    fun EmailVerificationScreenPreview() {
//        AlumniSystemTheme{
//            EmailVerificationScreen { }
//        }
//    }



        @Composable
        fun AppNavGraphPreview() {
            AlumniSystemTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController, sharedViewModel = viewModel())
            }
        }


















