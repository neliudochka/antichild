package com.example.antichild.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ButtonActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "com.example.antichild.ACTION_BUTTON") {
            Toast.makeText(context, "Button clicked!", Toast.LENGTH_SHORT).show()
        }
    }
}
