package com.example.smsencrypted.Screens

import android.content.Context
import android.provider.Telephony
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.smsencrypted.Data.Message
import com.example.smsencrypted.Data.User

@Composable
fun MessagesScreen(navController: NavController, number : String) {
    val messages = getSMSforUser(number, navController.context)
    Column {
        Text(text = number)
        messages.forEach {
            Text(text = it.body)
        }
    }

}
fun getSMSforUser(user: String, context: Context): List<Message>{
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