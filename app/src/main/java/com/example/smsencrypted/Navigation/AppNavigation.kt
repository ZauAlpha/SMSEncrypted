package com.example.smsencrypted.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.smsencrypted.Screens.MessageCard
import com.example.smsencrypted.Screens.MessagesScreen

import com.example.smsencrypted.Screens.SendMessageScreen
import com.example.smsencrypted.Screens.SecondScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.SendMessage.route) {
        composable(AppScreens.SendMessage.route) {
            SendMessageScreen(navController = navController)
        }
        composable(AppScreens.SecondScreen.route) {
            SecondScreen(navController = navController)
        }
        composable(
            AppScreens.MessagesScreen.route + "/{number}",
            arguments = listOf(navArgument("number") {
                type = NavType.StringType
            })
        ){
            MessagesScreen(navController, it.arguments?.getString("number") ?: "")

    }
    }
}


