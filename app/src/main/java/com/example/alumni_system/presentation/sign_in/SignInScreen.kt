package com.example.alumni_system.presentation.sign_in

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.alumni_system.nav.Screens

@Composable
fun SignInScreen(
    navController: NavController,
    viewModel: SignInViewModel = viewModel(),
    state: SignInState,
    onSignInClick: (String, String) -> Unit

) {
    val state by viewModel.signInState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Sign In", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        // Email Input
        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.updateEmail(it) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Password Input
        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.updatePassword(it) },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Sign In Button
        Button(
            onClick = {
                viewModel.signIn(
                    onNavigateToAdminScreen = { navController.navigate(Screens.AdminScreen.route) },
                    onNavigateToAlumniScreen = { navController.navigate(Screens.AlumniScreen.route) }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Sign In")
            }
        }

        // Show Error Message
        state.errorMessage?.let {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}