package com.example.alumni_world.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.alumni_world.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SharedViewModel(
    private val oneTapClient: SignInClient) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    // Function to start the Google Sign-In process
    fun signInWithGoogle(
        activity: Activity,
        oneTapClient: SignInClient,
        signInLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        viewModelScope.launch {
            try {
                val signInRequest = BeginSignInRequest.builder()
                    .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                            .setSupported(true)
                            .setServerClientId(activity.getString(R.string.web_client_id)) // Use your web client ID
                            .setFilterByAuthorizedAccounts(false)
                            .build()
                    )
                    .build()

                val signInResult = oneTapClient.beginSignIn(signInRequest).await()
                val intentSenderRequest = IntentSenderRequest.Builder(signInResult.pendingIntent.intentSender).build()
                signInLauncher.launch(intentSenderRequest)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Handle the result from Google Sign-In
    fun handleSignInResult(data: Intent) {
        viewModelScope.launch {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(data)
                val googleIdToken = credential.googleIdToken
                val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)

                val authResult = auth.signInWithCredential(googleCredentials).await()
                val user = authResult.user
                if (user != null) {
                    // Successful Google Sign-In
                    // Handle successful authentication here
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

    fun saveData(
        userData: UserData,
        context: Context


    ) = CoroutineScope(Dispatchers.IO).launch {

        val fireStoreRef = Firebase.firestore
            .collection(userData.toString())
            .document(userData.userID)

        try {
            fireStoreRef.set(userData)
                .addOnSuccessListener {
                    Toast.makeText(context, "Successfully posted data", Toast.LENGTH_SHORT).show()

                }
        } catch (e: Exception) {

            Toast.makeText(
                context, e.message,

                Toast.LENGTH_SHORT
            ).show()
        }

    }

    fun retrieveData(
        userID:String,
        context: Context,
        data: (UserData) -> Unit

    ) = CoroutineScope(Dispatchers.IO).launch {

        val fireStoreRef = Firebase.firestore
            .collection("user")
            .document(userID)

        try {
            fireStoreRef.get()
                .addOnSuccessListener {
                    if(it.exists()){
                        val userData = it.toObject<UserData>()!!
                        data(userData)


                    } else{
                        Toast.makeText(context, "Successfully fetched data", Toast.LENGTH_SHORT).show()


                    }


                }
        } catch (e: Exception) {

            Toast.makeText(
                context, e.message,

                Toast.LENGTH_SHORT
            ).show()
        }

    }


    fun deleteData(
        userID:String,
        navController: NavController,
        context: Context,


        ) = CoroutineScope(Dispatchers.IO).launch {

        val fireStoreRef = Firebase.firestore
            .collection("user")
            .document(userID)

        try {
            fireStoreRef.delete()
                .addOnSuccessListener{
                    Toast.makeText(context, "Successfully deleted data", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()


                }
        } catch (e: Exception) {

            Toast.makeText(
                context, e.message,

                Toast.LENGTH_SHORT
            ).show()
        }

    }



