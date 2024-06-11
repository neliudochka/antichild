package com.example.antichild.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.antichild.MainActivity
import com.example.antichild.models.ChildRecord
import com.example.antichild.models.ParentRecord
import com.example.antichild.utils.SharedPreferencesHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale

class MotionAlarmNotification(private val context: Context) {

    fun createMotionRecordChild(childRecord: ChildRecord) {
        val parentUid = SharedPreferencesHelper.getChildData().parentUid
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid

        val ref = FirebaseDatabase
            .getInstance()
            .getReference("/activity/alarm/${parentUid}/${currentUser}")
            .push()

        ref.setValue(childRecord)
            .addOnSuccessListener {
                Log.d("MotionRecordChild", "Notification sent successfully")
            }
            .addOnFailureListener { e ->
                Log.e("MotionRecordChild", "Error sending notification", e)
            }
    }

    fun createParentRecord(parentRecord: ParentRecord, childUid: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid

        val ref = FirebaseDatabase
            .getInstance()
            .getReference("/activity/alarm/${currentUser}/${childUid}")
            .push()

        ref.setValue(parentRecord)
            .addOnSuccessListener {
                Log.d("MotionRecordParent", "Alarm stop successfully")
            }
            .addOnFailureListener { e ->
                Log.e("MotionRecordParent", "Error stopping alarm", e)
            }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "motion_alarm_channel",
                "Motion Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for motion alarm notifications"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}