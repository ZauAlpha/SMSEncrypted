package com.example.smsencrypted.Screens


import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smsencrypted.Data.Data
import com.example.smsencrypted.Data.Message
import com.example.smsencrypted.Data.User
import com.example.smsencrypted.Navigation.AppScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondScreen(navController: NavController) {
    Scaffold(bottomBar = {
        CommonBottomAppBar(
            onClickOne = { navController.navigate(AppScreens.SendMessage.route) },
            onClickTwo = { },
            state = BottomNavigationScreens.Inbox
        )
    }) {
        BodyContent(modifier = Modifier.padding(it), navController = navController)
    }
}

@Composable
private fun BodyContent(modifier: Modifier =Modifier , navController: NavController) {

    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items(Data.users.size) { index ->
            val user = Data.users.elementAt(index)
            UserCard(user = user){
                navController.navigate(AppScreens.MessagesScreen.route + "/${user.number}")
            }

        }
        if (Data.users.isEmpty()) {
            item {
                Text(text = "No Messages")
            }
        }
    }

}

@Composable
fun UserCard(user: User, onClick: () -> Unit = {}) {
    val context = LocalContext.current

        ElevatedCard(
            modifier = Modifier
                .clickable { onClick() }.padding(4.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Text(text = user.number, modifier = Modifier.padding(4.dp))
        }


}

@Composable
fun MessageCard(message: Message) {
    Column(modifier = Modifier.padding(4.dp)) {
        Text(text = message.body)
        Text(text = message.address)
        Text(text = message.date)
    }

}

@Preview
@Composable
fun PreviewMessageCard() {
    MessageCard(message = Message(1, "Hello", "1234567890", "12/12/2021"))
}

@Preview
@Composable
fun UserCardPreview() {
    UserCard(user = User("+522225073205"))
}

