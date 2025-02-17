package com.example.alumni_system

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.alumni_system.nav.NavGraph
import com.example.alumni_system.nav.Screens
import com.example.alumni_system.ui.theme.AlumniSystemTheme
import com.example.alumni_system.utils.SharedViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavHostController
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
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

                    // Main App UI with Sidebar
                    AppWithSidebar(navController, sharedViewModel)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppWithSidebar(navController: NavHostController, sharedViewModel: SharedViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onLogout = {
                    // Handle logout logic here
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screens.LoginScreen.route) {
                        popUpTo(0)
                    }
                },
                onClose = {
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Scaffold(

        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                // Navigation graph for your app
                NavGraph(navController = navController, sharedViewModel = sharedViewModel)
            }
        }
    }
}

@Composable
fun DrawerContent(onLogout: () -> Unit, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Profile Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberImagePainter(
                    data = "https://via.placeholder.com/150" // Replace with your image URL
                ),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Logout Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogout() }
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Logout",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    }
}


