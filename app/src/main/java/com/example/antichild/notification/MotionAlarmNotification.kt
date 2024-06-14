package com.example.antichild.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
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

    private fun getChildrenIds(callback: NotificationCallback) {
        val parentUid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("activity/alarm/$parentUid")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (childSnapshot in snapshot.children) {
                        getNotificationData(object : NotificationCallback {
                            override fun onNotificationReceived(childRecord: ChildRecord?) {
                                if (childRecord != null) {
                                    callback.onNotificationReceived(childRecord)
                                } else {
                                    Log.d("MotionAlarmNotification", "No notification data available")
                                }
                            }
                        }, childSnapshot.key.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("MotionDetectionAlarm", "Failed to read value.", error.toException())
                callback.onNotificationReceived(null)
            }
        })
    }

    fun getNotificationData(callback: NotificationCallback, childUid: String) {
        val parentUid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/activity/alarm/$parentUid/$childUid")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.d("MotionAlarmNotification", snapshot.toString())

                    for (messSnapshot in snapshot.children) {
                        Log.d("MotionAlarmNotification", messSnapshot.toString())
                        val childNotification = messSnapshot.getValue(ChildRecord::class.java)
                        Log.d("MotionChildNotification", childNotification.toString())
                        if (childNotification != null) {
                            val isRead = messSnapshot.child("read").value

                            if (isRead == false) {
                                messSnapshot.ref.child("read").setValue(true)
                                callback.onNotificationReceived(childNotification)
                            } else {
                                callback.onNotificationReceived(null)
                            }
                        }
                    }
                } else {
                    callback.onNotificationReceived(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("MotionDetectionAlarm", "Failed to read value.", error.toException())
                callback.onNotificationReceived(null)
            }
        })
    }

    interface NotificationCallback {
        fun onNotificationReceived(childRecord: ChildRecord?)
    }

    fun createNotification(childRecord: ChildRecord) {
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("fragment", "ParentMotionDetectionFragment")
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val replyLabel: String = context.getString(android.R.string.ok)
        val remoteInput = RemoteInput.Builder(KEY_TEXT_REPLY).run {
            setLabel(replyLabel)
            build()
        }

        val replyIntent = Intent(context, ButtonActionReceiver::class.java).apply {
            action = "com.example.antichild.ACTION_REPLY"
            putExtra("notification_id", childRecord.date.hashCode())
            putExtra("child_id", childRecord.fromUid)
        }

        val replyPendingIntent = PendingIntent.getBroadcast(
            context, 0, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val action = NotificationCompat.Action.Builder(
            android.R.drawable.ic_input_add,
            "Turn off",
            replyPendingIntent
        ).addRemoteInput(remoteInput).build()

        val channelId = "Default"
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle(childRecord.title)
            .setContentText(childRecord.body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(action)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Default channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        val notificationId = childRecord.date.hashCode()

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    fun getNotificationParent() {
        getChildrenIds(object : NotificationCallback {
            override fun onNotificationReceived(childRecord: ChildRecord?) {
                if (childRecord != null) {
                    createNotification(childRecord)
                } else {
                    Log.d("MotionAlarmNotification", "No notification data available")
                }
            }
        })
    }

    fun createParentRecord(childUid: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        val parentRecord = createParentRecordModel()

        val ref = FirebaseDatabase
            .getInstance()
            .getReference("/activity/alarm/${childUid}/${currentUser}")
            .push()

        ref.setValue(parentRecord)
            .addOnSuccessListener {
                Log.d("MotionRecordParent", "Alarm stop successfully")
            }
            .addOnFailureListener { e ->
                Log.e("MotionRecordParent", "Error stopping alarm", e)
            }
    }

    interface ParentNotificationCallback {
        fun onNotificationReceived(parentRecord: ParentRecord?)
    }

    private fun createParentRecordModel(): ParentRecord {
        val uid = FirebaseAuth.getInstance().uid.toString()
        val calendar = Calendar.getInstance()
        val date = calendar.time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDate = dateFormat.format(date)

        return ParentRecord(uid, formattedDate)
    }

    fun getParentStopAlarmMessage() {

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

    companion object {
        const val KEY_TEXT_REPLY = "key_text_reply"
    }
}
