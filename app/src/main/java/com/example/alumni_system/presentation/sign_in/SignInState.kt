package com.example.alumni_system.presentation.sign_in

data class SignInState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val signInError: String = ""
)
