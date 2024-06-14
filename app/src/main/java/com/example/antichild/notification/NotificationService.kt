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
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.antichild.models.ChildRecord
import com.example.antichild.utils.SharedPreferencesHelper

class NotificationService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, createNotification())
        checkForNotifications()
    }

    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Notification Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle("Notification Service")
            .setContentText("Checking for new notifications")
            .setOngoing(true)
            .build()
    }

    private fun checkForNotifications() {
        runnable = object : Runnable {
            override fun run() {
                val motionAlarmNotification = MotionAlarmNotification(this@NotificationService)
                val userdata = SharedPreferencesHelper.getUserData()
                if (userdata.role == "parent") {
                    motionAlarmNotification.getNotificationParent()
                }

                handler.postDelayed(this, 10000)
            }
        }

        handler.post(runnable!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        runnable?.let {
            handler.removeCallbacks(it)
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "NotificationServiceChannel"
    }
}
