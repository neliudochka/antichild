package com.example.antichild.notification

import android.app.NotificationManager
import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.antichild.MainActivity
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
            } else {
                Toast.makeText(context, "Wrong password!", Toast.LENGTH_SHORT).show()
                openFragment(context, "ParentMotionDetectionFragment")
            }

            val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(intent.getIntExtra("notification_id", 0))
        } else if (intent?.action == MotionAlarmNotification.ACTION_READ) {
            val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(intent.getIntExtra("notification_id", 0))
        }
    }

    private fun validatePassword(password: String): Boolean {
        return if (password.isEmpty()) {
            false
        } else password == SharedPreferencesHelper.getParentData().accessPassword
    }

    private fun openFragment(context: Context?, fragmentName: String) {
        if (context != null) {
            val intent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("fragment", fragmentName)
            }
            context.startActivity(intent)
        }
    }
}
