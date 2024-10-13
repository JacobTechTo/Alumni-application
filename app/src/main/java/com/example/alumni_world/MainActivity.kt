package com.example.alumni_world

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.alumni_world.nav.NavGraph
import com.example.alumni_world.nav.Screens
import com.example.alumni_world.ui.theme.Alumni_worldTheme
import com.example.alumni_world.utils.SharedViewModel
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavHostController
    private val sharedViewModel: SharedViewModel by viewModels()

    // Initialize Google Sign-In client
    private lateinit var oneTapClient: SignInClient

    // Register an ActivityResultLauncher to handle the Google Sign-In result
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // Pass the data to the ViewModel to handle the sign-in result
            result.data?.let { data ->
                sharedViewModel.handleSignInResult(data)
            }
        } else {
            // Handle sign-in failure if needed
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        enableEdgeToEdge()

        setContent {
            Alumni_worldTheme {
                // Initialize navController inside the Composable function
                navController = rememberNavController()

                // Observe the current user state
                val currentUser = auth.currentUser

                // Use LaunchedEffect to navigate based on user authentication
                LaunchedEffect(currentUser) {
                    when {
                        currentUser == null -> {
                            navController.navigate(Screens.LoginScreen.route)
                        }
                        !currentUser.isEmailVerified -> {
                            navController.navigate(Screens.EmailVerificationScreen.route)
                        }
                        else -> {
                            navController.navigate(Screens.DashboardScreen.route)
                        }
                    }
                }

                // Surface and NavGraph setup
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(navController = navController, sharedViewModel = sharedViewModel,
                        onGoogleSignInClick = { beginGoogleSignIn() }  )
                }
            }
        }
    }
    // Function to start Google Sign-In
    private fun beginGoogleSignIn() {
        sharedViewModel.signInWithGoogle(this, oneTapClient, signInLauncher)
    }
}
