package com.example.hotelloginsystem.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.alumni_world.nav.Screens


@Composable

fun MainScreen(navController: NavController){


    Column (
        modifier = Modifier
            .padding(start = 50.dp, end = 50.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment =Alignment.CenterHorizontally
    ) {
        Button( modifier = Modifier, onClick = {
            navController.navigate(route = Screens.GetDataScreen.route)
        }) {
            //Get user data

            Text(text = "Get User Data")
            
        }


    }




}

