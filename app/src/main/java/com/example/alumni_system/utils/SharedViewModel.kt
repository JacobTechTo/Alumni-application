package com.example.alumni_system.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SharedViewModel() : ViewModel() {
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


    @SuppressLint("SuspiciousIndentation")
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
}