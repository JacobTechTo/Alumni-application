package com.example.alumni_system.nav


import AlumniScreen
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.alumni_system.presentation.sign_in.SignInScreen
import com.example.alumni_system.presentation.sign_in.SignInState
import com.example.alumni_system.screens.AddDataScreen
import com.example.alumni_system.screens.AdminScreen
import com.example.alumni_system.screens.AlumniDirectoryScreen
import com.example.alumni_system.screens.CreatePortfolioScreen
import com.example.alumni_system.screens.EditJobScreen
import com.example.alumni_system.screens.EmailVerificationScreen
import com.example.alumni_system.screens.GetDataScreen
import com.example.alumni_system.screens.JobSearchScreen
import com.example.alumni_system.screens.LoginScreen
import com.example.alumni_system.screens.MainScreen
import com.example.alumni_system.screens.ManageJobsScreen
import com.example.alumni_system.screens.ManageRolesScreen
import com.example.alumni_system.screens.ManageUsersScreen
import com.example.alumni_system.screens.ModerateContentScreen
import com.example.alumni_system.screens.PostJobsScreen
import com.example.alumni_system.screens.PublishProjectsScreen
import com.example.alumni_system.screens.SuperUserScreen
import com.example.alumni_system.screens.UpdateProfileScreen
import com.example.alumni_system.screens.ViewProfileScreen
import com.example.alumni_system.utils.SharedViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.alumni_system.nav.Screens.JobSearchScreen
import com.example.alumni_system.screens.AlumniProfileScreen
import com.example.alumni_system.screens.AnalyticsScreen
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun NavGraph(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
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

      //Admin Screen
        composable(route = Screens.AdminScreen.route) {
            AdminScreen(navController = navController)
        }

      //Alumni Screen
        composable(route = Screens.AlumniScreen.route) {
            AlumniScreen(navController = navController)
        }

        //SuperUser Screen
        composable(route= Screens.SuperUserScreen.route){
            SuperUserScreen(navController =navController)
        }

        //Alumni Profile
        composable(route =Screens.AlumniProfileScreen.route){
            AlumniProfileScreen(navController =navController, uid= "uid", applicationId= "applicationId", email="email")
        }

        //ManageRolesScreen
        composable(route= Screens.ManageRolesScreen.route){
            ManageRolesScreen(navController =navController)
        }

        //ManageUsersScreen
        composable(route= Screens.ManageUsersScreen.route){
            ManageUsersScreen(navController= navController)
        }


        composable(route= Screens.ModerateContentScreen.route){
            ModerateContentScreen(navController= navController)
        }

        composable(route= Screens.PostJobsScreen.route){
            PostJobsScreen(navController= navController)
        }

        // In your NavGraph or wherever you define the routes
        composable(route = "${Screens.EditJobScreen.route}/{jobId}") { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            EditJobScreen(navController = navController, jobId = jobId)
        }



        composable(route = "create_portfolio_screen/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email")
            CreatePortfolioScreen(navController = navController, email = email)
        }

        composable(route = "update_profile_screen/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email")
            UpdateProfileScreen(navController = navController, userId = email ?: "")
        }

        composable(route = "view_profile_screen/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email")
            ViewProfileScreen(navController = navController, email = email ?: "")
        }




        composable(route= Screens.ManageJobsScreen.route){
            ManageJobsScreen(navController= navController)
        }

        //EmailVerificationScreen
        composable(route = Screens.EmailVerificationScreen.route) {
            EmailVerificationScreen(navController = navController)
        }

        //AlumniDirectoryScreen
        composable(route = Screens.AlumniDirectoryScreen.route){
            AlumniDirectoryScreen(navController= navController)
        }

        //CreatePortfolioScreen
        composable(route = Screens.CreatePortfolioScreen.route){
            CreatePortfolioScreen(navController= navController)
        }

        //PublishProjectsScreen
        composable(route = Screens.PublishProjectsScreen.route){
            PublishProjectsScreen(navController =navController)
        }

        //AnalyticsScreen
        composable(route = Screens.AnalyticsScreen.route){
            AnalyticsScreen(navController =navController)
        }


        // JobSearchScreen navigation (no alumniId)
        composable(
            route = Screens.JobSearchScreen.route
        ) {
            JobSearchScreen(navController = navController, uid = "uid" )
        }



        //SignInScreen
        composable(route = Screens.SignInScreen.route) {
            var signInError = remember { mutableStateOf<String?>(null) }

            SignInScreen(
                navController,
                state = SignInState(signInError = signInError.value.toString()),
                onSignInClick = { email, password ->
                    val auth = FirebaseAuth.getInstance()
                    val db = FirebaseFirestore.getInstance()

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid
                                if (userId != null) {
                                    db.collection("users").document(userId).get()
                                        .addOnSuccessListener { document ->
                                            val role = document.getString("role")
                                            if (role == "admin") {
                                                navController.navigate(Screens.AdminScreen.route) // Navigate to Admin Home
                                            } else {
                                                navController.navigate(Screens.AlumniScreen.route) // Navigate to Alumni Home
                                            }
                                        }
                                }
                            } else {
                                signInError.value = task.exception?.message ?: "Sign-in failed."
                            }
                        }
                }
            )
        }



        //LoginScreen
        composable(route = Screens.LoginScreen.route){
            LoginScreen(navController = navController)
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