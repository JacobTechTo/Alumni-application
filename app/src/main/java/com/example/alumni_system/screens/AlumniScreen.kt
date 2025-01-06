import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.alumni_system.R
import com.example.alumni_system.nav.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun AlumniScreen(navController: NavHostController) {
    val jobs = remember { mutableStateListOf<Map<String, Any>>() }
    val approvedPosts = remember { mutableStateListOf<Map<String, Any>>() }
    val db = FirebaseFirestore.getInstance()

    // Profile picture state
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        profileImageUri = uri // Update the profile image URI
    }

    // Fetch jobs and approved posts
    LaunchedEffect(Unit) {
        try {
            val jobsResult = db.collection("jobs").get().await()
            jobs.clear()
            for (document in jobsResult) {
                jobs.add(document.data)
            }

            val postsResult = db.collection("posts").whereEqualTo("isApproved", true).get().await()
            approvedPosts.clear()
            for (document in postsResult) {
                approvedPosts.add(document.data)
            }
        } catch (e: Exception) {
            e.printStackTrace() // Log the error for debugging
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alumni Panel") },
                actions = {
                    // Profile settings and picture click
                    IconButton(onClick = { launcher.launch("image/*") }) {
                        if (profileImageUri != null) {
                            Image(
                            painter = rememberAsyncImagePainter(model = profileImageUri),
                            contentDescription = "Profile",
                            modifier = Modifier,
                            contentScale = ContentScale.Crop
                        )

                        } else {
                            Icon(
                                painter = painterResource(R.drawable.ic_launcher_background), // Replace with your default profile icon
                                contentDescription = "Profile"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Welcome Section
            Text(
                text = "Welcome, Alumni!",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Profile Section with Navigation
            Button(
                onClick = { navController.navigate(Screens.AlumniDirectoryScreen.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Alumni Directory")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { navController.navigate(Screens.UpdateProfileScreen.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Profile")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { navController.navigate(Screens.CreatePortfolioScreen.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create / Update Portfolio")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { navController.navigate(Screens.PublishProjectsScreen.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Publish Projects")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { navController.navigate(Screens.JobSearchScreen.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Job Search and Applications")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Alumni Dashboard Content
            Text(
                text = "Alumni Dashboard",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Display Jobs Section
            Text("Jobs", style = MaterialTheme.typography.titleLarge)
            if (jobs.isEmpty()) {
                Text("No jobs available.")
            } else {
                LazyColumn {
                    items(jobs) { job ->
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Title: ${job["title"]}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Description: ${job["description"]}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Divider()
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display Approved Posts Section
            Text("Approved Posts", style = MaterialTheme.typography.titleLarge)
            if (approvedPosts.isEmpty()) {
                Text("No approved posts available.")
            } else {
                LazyColumn {
                    items(approvedPosts) { post ->
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Post: ${post["content"]}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Divider()
                    }
                }
            }
        }
    }
}
