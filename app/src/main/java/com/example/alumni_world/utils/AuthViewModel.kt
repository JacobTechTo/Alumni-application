package com.example.alumni_world.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val authState: StateFlow<Boolean> = authStateFlow()

    private fun authStateFlow(): StateFlow<Boolean> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser != null)
        }
        auth.addAuthStateListener(authStateListener)

        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, auth.currentUser != null)

    fun signUp(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Send email verification
                    auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                        if (verifyTask.isSuccessful) {
                            onSuccess()
                        } else {
                            onError("Verification email sending failed.")
                        }
                    }
                } else {
                    onError("Sign-up failed: ${task.exception?.message}")
                }
            }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Login failed: ${task.exception?.message}")
                }
            }
    }

    fun reloadUser(onSuccess: (FirebaseUser) -> Unit, onError: (String) -> Unit) {
        val currentUser = auth.currentUser
        currentUser?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess(currentUser)
            } else {
                onError("Failed to reload user: ${task.exception?.message}")
            }
        }
    }
}
