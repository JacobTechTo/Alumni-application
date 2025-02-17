package com.example.alumni_system.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.alumni_system.nav.Screens
import com.example.alumni_system.ui.components.DropdownMenuContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun AdminScreen(navController: NavHostController) {
    TopAppBar(
        title = { Text("Admin Panel") },
        actions = {
            DropdownMenuContent(navController = navController, onLogout = { handleLogout(navController) })
        }
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {


        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate(Screens.ManageUsersScreen.route)
        }) {
            Text("Manage Users")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate(Screens.PostJobsScreen.route)
        }) {
            Text("Post Job")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate(Screens.ManageJobsScreen.route)
        }) {
            Text("Manage Job Postings")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate(Screens.ModerateContentScreen.route)
        }) {
            Text("Moderate Content")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate(Screens.AnalyticsScreen.route)
        }) {
            Text("See Analytics")
        }

    }
}

fun handleLogout(navController: NavHostController) {
    navController.navigate("login_screen") {
        popUpTo(0) // Clear back stack
    }
}



