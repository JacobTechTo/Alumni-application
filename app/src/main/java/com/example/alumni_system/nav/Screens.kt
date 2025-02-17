package com.example.alumni_system.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.alumni_system.presentation.profile.ProfileScreen


sealed class Screens(val route: String) {
    object SignInScreen: Screens(route = "signin_screen")
    object LoginScreen: Screens(route = "login_screen")
    object AdminScreen: Screens(route = "admin_screen")
    object AlumniScreen: Screens(route = "alumni_screen")
    object MainScreen: Screens(route = "main_screen")
    object AddDataScreen: Screens(route = "add_data_screen")
    object GetDataScreen: Screens(route = "get_data_screen")
    object SuperUserScreen: Screens(route = "superuser_screen")
    object  ManageUsersScreen: Screens(route = "manage_users_screen")
    object ManageRolesScreen: Screens(route = "manage_roles_screen")
    object ManageJobsScreen: Screens(route = "manage_jobs_screen")
    object ModerateContentScreen: Screens(route = "manage_content_screen")
    object PostJobsScreen: Screens(route = "post_jobs_screen")
    object JobSearchScreen: Screens(route = "job_search_screen")
    object AlumniDirectoryScreen: Screens(route = "alumni_directory_screen")
    object PublishProjectsScreen: Screens(route = "publish_projects_screen")
    object UpdateProfileScreen: Screens(route = "update_profile_screen")
    object CreatePortfolioScreen: Screens(route = "create_portfolio_screen")
    object EditJobScreen: Screens(route = "edit_job_screen")
    object AnalyticsScreen: Screens(route = "analytics_screen")
    object AlumniProfileScreen: Screens(route ="alumni_profile")

    object EmailVerificationScreen : Screens(route ="email_verification_screen" )


    // Add a function to generate the dynamic route for EditJobScreen
    fun editJobScreenRoute(jobId: String): String {
        return "edit_job_screen/$jobId"
    }

}
