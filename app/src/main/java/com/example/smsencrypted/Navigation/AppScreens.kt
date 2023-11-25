package com.example.smsencrypted.Navigation

sealed class AppScreens(val route: String){
    object SendMessage: AppScreens("first_screen")
    object SecondScreen: AppScreens("second_screen")
    object MessagesScreen: AppScreens("messages_screen")

}
