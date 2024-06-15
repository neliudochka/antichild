package com.example.antichild.notification

import android.app.NotificationManager
import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.antichild.utils.SharedPreferencesHelper

class ButtonActionReceiver : BroadcastReceiver() {
    private lateinit var motionAlarmNotification: MotionAlarmNotification
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == MotionAlarmNotification.ACTION_REPLY) {
            val remoteInput = RemoteInput.getResultsFromIntent(intent)
            val remoteText = remoteInput.getCharSequence(MotionAlarmNotification.KEY_TEXT_REPLY).toString()

            if (validatePassword(remoteText)) {
                val childUid = intent.getStringExtra("child_id")
                motionAlarmNotification = MotionAlarmNotification(context!!)

                if (childUid != null) {
                    motionAlarmNotification.createParentRecord(childUid)
                }

                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(intent.getIntExtra("notification_id", 0))
            } else {
                Toast.makeText(context, "Wrong password!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validatePassword(password: String): Boolean {
        return if (password.isEmpty()) {
            false
        } else password == SharedPreferencesHelper.getParentData().accessPassword
    }
}
