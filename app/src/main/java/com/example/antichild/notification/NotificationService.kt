package com.example.antichild.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.antichild.models.ChildRecord
import com.example.antichild.utils.SharedPreferencesHelper

class NotificationService : Service() {

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())
        checkForNotifications()
    }

    private fun createNotification(): Notification {
        val notificationChannelId = "NOTIFICATION_CHANNEL_ID"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Notification Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
        return notificationBuilder.setOngoing(true)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle("Notification Service")
            .setContentText("Checking for new notifications")
            .build()
    }

    private fun checkForNotifications() {
        val handler = Handler(Looper.getMainLooper())
        val runnable: Runnable = object : Runnable {
            override fun run() {
                val motionAlarmNotification = MotionAlarmNotification(this@NotificationService)
                val userdata = SharedPreferencesHelper.getUserData()
                if (userdata.role == "parent") {
                    motionAlarmNotification.getNotificationParent()
                }

                handler.postDelayed(this, 10000)
            }
        }
        handler.post(runnable)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}