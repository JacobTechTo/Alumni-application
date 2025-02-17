package com.example.alumni_system.presentation.sign_in

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {

    private val _signInState = MutableStateFlow(SignInState())
    val signInState: StateFlow<SignInState> = _signInState

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun updateEmail(email: String) {
        _signInState.value = _signInState.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        _signInState.value = _signInState.value.copy(password = password)
    }

    fun signIn(
        onNavigateToAdminScreen: () -> Unit,
        onNavigateToAlumniScreen: () -> Unit
    ) {
        val email = _signInState.value.email
        val password = _signInState.value.password

        _signInState.value = _signInState.value.copy(isLoading = true)

        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid ?: return@addOnCompleteListener

                        // Retrieve user role from Firestore
                        firestore.collection("users").document(userId).get()
                            .addOnSuccessListener { document ->
                                val role = document.getString("role")
                                if (role == "admin") {
                                    onNavigateToAdminScreen()
                                } else {
                                    onNavigateToAlumniScreen()
                                }
                            }
                            .addOnFailureListener {
                                _signInState.value = _signInState.value.copy(
                                    isLoading = false,
                                    errorMessage = "Failed to retrieve user role."
                                )
                            }
                    } else {
                        _signInState.value = _signInState.value.copy(
                            isLoading = false,
                            errorMessage = task.exception?.message ?: "Sign-in failed."
                        )
                    }
                }
        }
    }
}
