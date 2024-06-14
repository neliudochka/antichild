package com.example.antichild.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ButtonActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "com.example.antichild.ACTION_REPLY") {
            Log.d("ButtonActionReceiver", "Message received!")
        }
    }
}
