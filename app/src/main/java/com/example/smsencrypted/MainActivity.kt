package com.example.smsencrypted

import android.content.Context
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Telephony
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavArgs
import com.example.smsencrypted.Data.Data
import com.example.smsencrypted.Data.Encryption
import com.example.smsencrypted.Data.Message
import com.example.smsencrypted.Data.User
import com.example.smsencrypted.Navigation.AppNavigation
import com.example.smsencrypted.ui.theme.SMSEncryptedTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : ComponentActivity() {


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RequestSendMessage()
        RequestReadMessage()
        RequestReceiveMessage()
        // recive preferences from datastore of keys
        val keys = getKeys()
        //if keys are not stored generate new keys and store them
        lifecycleScope.launch(Dispatchers.IO) {
            keys.collect { value ->
                if(!value.stored){
                    Data.keys = Encryption.generateKeys()
                    setPreferences()
                }else{
                    //if keys are stored set them to Data.keys
                    Data.keys[0] = value.e
                    Data.keys[1] = value.n
                    Data.keys[2] = value.d
                    Data.keys[3] = value.p
                    Data.keys[4] = value.q
                    Data.keys[5] = value.dP
                    Data.keys[6] = value.dQ
                    Data.keys[7] = value.qInv
                }
            }
        }

        Data.users = getUsers()

        setContent {
            SMSEncryptedTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var count by remember { mutableStateOf(0) }
                    var lastBody by remember { mutableStateOf("") }
                    val messageList = getAllSms()
                    AppNavigation()
                }


            }
        }
    }

    suspend fun setPreferences() {
        /*
    *     e = 0
    *     n = 1
    *     d = 2
    *     p = 3
    *     q = 4
    *     dP = 5
    *     dQ = 6
    *     qInv = 7
    * */

        dataStore.edit { settings ->
            settings[longPreferencesKey("e")] = Data.keys[0]
            settings[longPreferencesKey("n")] = Data.keys[1]
            settings[longPreferencesKey("d")] = Data.keys[2]
            settings[longPreferencesKey("p")] = Data.keys[3]
            settings[longPreferencesKey("q")] = Data.keys[4]
            settings[longPreferencesKey("dp")] = Data.keys[5]
            settings[longPreferencesKey("dq")] = Data.keys[6]
            settings[longPreferencesKey("qinv")] = Data.keys[7]
            settings[booleanPreferencesKey("stored")] = true
        }

    }

    fun getKeys() = dataStore.data.map { preferences ->
        val e = preferences[longPreferencesKey("e")] ?: 0
        val n = preferences[longPreferencesKey("n")] ?: 0
        val d = preferences[longPreferencesKey("d")] ?: 0
        val p = preferences[longPreferencesKey("p")] ?: 0
        val q = preferences[longPreferencesKey("q")] ?: 0
        val dp = preferences[longPreferencesKey("dp")] ?: 0
        val dq = preferences[longPreferencesKey("dq")] ?: 0
        val qinv = preferences[longPreferencesKey("qinv")] ?: 0
        val stored = preferences[booleanPreferencesKey("stored")] ?: false
        Keys(e, n, d, p, q, dp, dq, qinv, stored)
    }

    fun getAllSms(): List<Message> {
        val smsList = mutableListOf<Message>()

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

        applicationContext.contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            projection,
            null,
            null,
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

    fun getUsers(): Set<User> {
        val users = mutableSetOf<User>()
        val projection = arrayOf(
            Telephony.Sms._ID,
            Telephony.Sms.ADDRESS,
        )
        val sortOrder = "${Telephony.Sms.ADDRESS} DESC"

        applicationContext.contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(Telephony.Sms._ID)
            val addressColumn = cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val address = cursor.getString(addressColumn)
                val user = User(address)
                users.add(user)
            }
        }
        return users
    }


    fun RequestSendMessage() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.SEND_SMS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }

            else -> {
                requestPermissions(arrayOf(android.Manifest.permission.SEND_SMS), 0)
            }
        }
    }

    fun RequestReadMessage() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_SMS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }

            else -> {
                requestPermissions(arrayOf(android.Manifest.permission.READ_SMS), 0)
            }
        }
    }

    fun RequestReceiveMessage() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECEIVE_SMS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }

            else -> {
                requestPermissions(arrayOf(android.Manifest.permission.RECEIVE_SMS), 0)
            }
        }
    }

}

data class Keys(
    val e: Long,
    val n: Long,
    val d: Long,
    val p: Long,
    val q: Long,
    val dP: Long,
    val dQ: Long,
    val qInv: Long,
    val stored: Boolean
)