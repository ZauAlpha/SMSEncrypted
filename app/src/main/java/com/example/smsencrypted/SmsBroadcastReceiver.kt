package com.example.smsencrypted

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log


class SmsBroadcastReceiver(

) :
    BroadcastReceiver() {
    private lateinit var listener: (text:String)->Unit
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            var smsSender = ""
            var smsBody = ""
            for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                smsSender = smsMessage.displayMessageBody
                smsBody += smsMessage.messageBody
            }

            listener(smsSender)
        }
    }

    fun setEvent(function : (text:String)->Unit) {
        listener = function
    }

    interface Listener {
        fun onTextReceived(text: String?)
    }

    companion object {
        private const val TAG = "SmsBroadcastReceiver"
    }
}