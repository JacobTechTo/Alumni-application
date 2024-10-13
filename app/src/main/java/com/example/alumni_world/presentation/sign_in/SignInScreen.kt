package com.example.hotelloginsystem.presentation.sign_in

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.lang.reflect.Modifier



@Composable
 fun SignInScreen  () {
    val context = LocalContext.current

    @Composable
    fun SignInScreen(
        state: SignInState,
        onSignInClick: () -> Unit
    ) {
        val context = LocalContext.current
        LaunchedEffect(key1 = state.signInError) {
            state.signInError?.let { error ->
                Toast.makeText(
                    context,
                    error,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        Box(
            modifier = androidx.compose.ui.Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = onSignInClick) {
                Text(text = "Sign in")
            }
        }
    }
}