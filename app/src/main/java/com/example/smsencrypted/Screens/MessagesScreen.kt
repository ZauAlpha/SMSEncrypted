package com.example.smsencrypted.Screens

import android.content.Context
import android.provider.Telephony
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smsencrypted.Data.Data
import com.example.smsencrypted.Data.Encryption
import com.example.smsencrypted.Data.Message
import com.example.smsencrypted.Data.User
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext
import kotlin.system.measureTimeMillis

@Composable
fun MessagesScreen(navController: NavController, number: String) {
    val messages = getSMSforUser(number, navController.context)
    Column {
        Text(text = number)
        messages.forEach {
            MessageCard(author = number, content = it.body)
        }
    }

}

@Composable
fun MessageCard(author: String, content: String) {
    Card(
        modifier = Modifier
            .padding(all = 8.dp)
    ) {
        var text by remember { mutableStateOf(content) }
        val context = LocalContext.current

        Column {

            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp)
            )

            Button(onClick = {
                try {
                   val time = measureTimeMillis {
                       text = Encryption.decrypt(
                           stringOfLongsToLongArray(text),
                           Data.keys[5],
                           Data.keys[6],
                           Data.keys[7],
                           Data.keys[3],
                           Data.keys[4]
                       )
                   }
                    Toast.makeText(context, "Time: $time", Toast.LENGTH_SHORT).show()
                }
                catch (e: Exception){
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }

            }) {
                Text(text = "Decrypt CRT")
            }
            Button(onClick = {
                try {
                    val time = measureTimeMillis {
                        text = Encryption.decryptStandard(
                            stringOfLongsToLongArray(text),
                            Data.keys[2],
                            Data.keys[1])
                    }
                    Toast.makeText(context, "Time: $time", Toast.LENGTH_SHORT).show()
                }
                catch (e: Exception){
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }

            }) {
                Text(text = "Decrypt standard RSA")
            }
        }

    }
}

fun stringOfLongsToLongArray(string: String): LongArray {
    val numbers = string.split(",")
    val array = LongArray(numbers.size)
    for (i in 0 until numbers.size) {
        val number = numbers[i].replace("[", "").replace("]", "").trim()
        array[i] = number.toLong()
    }
    return array
}

fun getSMSforUser(user: String, context: Context): List<Message> {
    val smsList = mutableListOf<Message>()
    lateinit var selectionArgs: Array<String>

    // Definir las columnas que deseas recuperar
    val projection = arrayOf(
        Telephony.Sms._ID,
        Telephony.Sms.ADDRESS,
        Telephony.Sms.BODY,
        Telephony.Sms.DATE
    )
    // Ordenar los mensajes por fecha en orden descendente (los más recientes primero)
    val sortOrder = "${Telephony.Sms.DATE} DESC"

    // Consultar la base de datos de mensajes SMS

    context.contentResolver.query(
        Telephony.Sms.Inbox.CONTENT_URI,
        projection,
        "address = ?",
        arrayOf(user),
        sortOrder
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(Telephony.Sms._ID)
        val addressColumn = cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
        val bodyColumn = cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)
        val dateColumn = cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val address = cursor.getString(addressColumn)
            val body = cursor.getString(bodyColumn)
            val date = cursor.getLong(dateColumn)

            // Puedes hacer lo que desees con la información del mensaje, por ejemplo, agregarlo a la lista
            val message = Message(id.toInt(), body, address, date.toString())
            smsList.add(message)
        }
    }

    return smsList
}