package com.example.alumni_world.nav

import DashboardScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.alumni_world.screen.EmailVerificationScreen
import com.example.alumni_world.screen.LoginScreen
import com.example.alumni_world.utils.SharedViewModel
import com.example.hotelloginsystem.screen.AddDataScreen
import com.example.hotelloginsystem.screen.GetDataScreen
import com.example.hotelloginsystem.screen.MainScreen

@Composable

fun NavGraph(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    onGoogleSignInClick: () -> Unit // Include the parameter
) {
    NavHost(
        navController = navController,
        startDestination = Screens.LoginScreen.route
    ) {
        // main screen
        composable(
            route = Screens.MainScreen.route
        ) {
            MainScreen(
                navController = navController,

                )
        }

        //Dashboard Screen
        composable(route = Screens.DashboardScreen.route) {
            DashboardScreen(navController = navController)
        }

        //EmailVerificationScreen
        composable(route = Screens.EmailVerificationScreen.route) {
            EmailVerificationScreen(navController = navController)
        }

        //LoginScreen
        composable(route = Screens.LoginScreen.route){
            LoginScreen(navController = navController,
                onGoogleSignInClick = onGoogleSignInClick)
        }

        // get data screen
        composable(
            route = Screens.GetDataScreen.route
        ) {
            GetDataScreen(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }
        // add data screen
        composable(
            route = Screens.AddDataScreen.route
        ) {
            AddDataScreen(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }
    }
}