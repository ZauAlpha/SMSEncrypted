package com.example.smsencrypted.Screens


import android.content.Context
import android.telephony.SmsManager
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smsencrypted.Data.Data
import com.example.smsencrypted.Data.Encryption
import com.example.smsencrypted.Navigation.AppScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMessageScreen(navController: NavController) {
    Scaffold(bottomBar = {
        CommonBottomAppBar(
            onClickOne = { },
            onClickTwo = { navController.navigate(AppScreens.SecondScreen.route) },
            state = BottomNavigationScreens.NewMessage
        )
    }) {
        ViewSendMessage(modifier = Modifier.padding(it))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewSendMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Public Key: (${Data.keys[0]}, ${Data.keys[1]})")
        Text(text = "Send Message", modifier = Modifier.padding(16.dp))
        var number by remember { mutableStateOf("") }
        var n by remember { mutableStateOf("") }
        var e by remember { mutableStateOf("") }
        var text by remember { mutableStateOf("") }
        OutlinedTextField(
            value = number,
            onValueChange = { number = it },
            label = { Text("Number") },
            modifier = Modifier.padding(16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Text") },
            modifier = Modifier.padding(16.dp)
        )
        Text(text = "Introduce reciber public key")
        OutlinedTextField(
            value = n,
            onValueChange = { n = it },
            label = { Text("N") },
            modifier = Modifier.padding(16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = e,
            onValueChange = { e = it },
            label = { Text("e") },
            modifier = Modifier.padding(16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        SendMessageButton(
            modifier = Modifier.padding(16.dp),
            context = LocalContext.current,
            number = number,
            text = text, e, n, onClickSupport = {
                number = ""
                text = ""
            }
        )
    }
}

@Composable
fun SendMessageButton(
    modifier: Modifier = Modifier,
    context: Context,
    number: String,
    text: String,
    e: String,
    n: String,
    onClickSupport: () -> Unit = {}
) {
    Button(modifier = modifier, onClick = {
        val cipher = Encryption.encrypt(text, e.toLong(), n.toLong())
        if (cipher.contentToString().length >= 160) {
            Toast.makeText(context, "Ciphertext is to long", Toast.LENGTH_LONG).show()

        } else {
            try {

                SmsManager.getDefault()
                    .sendTextMessage(number, null, cipher.contentToString(), null, null)
                Toast.makeText(
                    context,
                    "Message size ${cipher.contentToString().length}}",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
            onClickSupport()
        }
    }
    ) {
        Text("Send Message")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun PreviewSendMessage() {
    Scaffold(bottomBar = {
        CommonBottomAppBar(
            onClickOne = { },
            onClickTwo = { },
            state = BottomNavigationScreens.NewMessage
        )
    }) {
        ViewSendMessage(modifier = Modifier.padding(it))
    }
}
