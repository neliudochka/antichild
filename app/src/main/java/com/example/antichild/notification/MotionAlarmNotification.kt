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

    fun createMotionRecordChild() {
        val childRecord = createChildRecordModel()
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

    private fun createChildRecordModel(): ChildRecord {
        val childUid = FirebaseAuth.getInstance().uid
        val username = SharedPreferencesHelper.getChildData().username
        val title = "Attention!"
        val body = "Motion is detected at ${username}'s device!"
        val calendar = Calendar.getInstance()
        val date = calendar.time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDate = dateFormat.format(date)

        return ChildRecord(childUid!!, title, body, formattedDate, childUid!!)
    }

    fun createNotification(childRecord: ChildRecord) {

        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val buttonIntent = Intent(context, ButtonActionReceiver::class.java).apply {
            action = "com.example.antichild.ACTION_BUTTON"
        }
        val buttonPendingIntent = PendingIntent.getBroadcast(
            context, 0, buttonIntent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "Default"
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle(childRecord.title)
            .setContentText(childRecord.body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_input_add, "Turn off", buttonPendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Default channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(0, notificationBuilder.build())
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