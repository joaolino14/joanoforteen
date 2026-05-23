package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.BuilderScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ResumeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val resumeViewModel: ResumeViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = "login",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate("dashboard") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("dashboard") {
                        DashboardScreen(
                            viewModel = resumeViewModel,
                            onNavigateToBuilder = { resumeId ->
                                navController.navigate("builder/$resumeId")
                            },
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("dashboard") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(
                        route = "builder/{resumeId}",
                        arguments = listOf(navArgument("resumeId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val resumeId = backStackEntry.arguments?.getLong("resumeId") ?: -1L
                        BuilderScreen(
                            resumeId = resumeId,
                            viewModel = resumeViewModel,
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
