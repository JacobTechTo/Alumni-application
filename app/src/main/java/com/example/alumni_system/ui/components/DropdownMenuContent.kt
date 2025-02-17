package com.example.alumni_system.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController

@Composable
fun DropdownMenuContent(
    navController: NavHostController,
    onLogout: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    navController.navigate("manage_account_screen")
                },
                text = { Text("Manage Account") }
            )
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onLogout()
                },
                text = { Text("Logout") }
            )
        }
    }
}
