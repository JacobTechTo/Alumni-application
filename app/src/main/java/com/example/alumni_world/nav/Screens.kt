package com.example.alumni_world.nav

sealed class Screens(val route: String) {
    object LoginScreen: Screens(route = "login_screen")
    object MainScreen:Screens(route = "main_screen")
    object AddDataScreen:Screens(route = "add_data_screen")
    object GetDataScreen:Screens(route = "get_data_screen")
    object DashboardScreen : Screens(route = "dashboard_screen")
    object EmailVerificationScreen : Screens(route ="email_verification_screen" )

}