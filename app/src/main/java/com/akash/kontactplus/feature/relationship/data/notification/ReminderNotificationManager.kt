package com.akash.kontactplus.feature.relationship.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.akash.kontactplus.MainActivity
import com.akash.kontactplus.R
import com.akash.kontactplus.feature.relationship.domain.model.RelationshipReminder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        private const val CHANNEL_ID_REMINDERS = "relationship_reminders"
        
        const val ACTION_MARK_DONE = "com.akash.kontactplus.ACTION_MARK_DONE"
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
    }

    init {
        createNotificationChannel()
    }

    fun showReminderNotification(reminder: RelationshipReminder) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(EXTRA_REMINDER_ID, reminder.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 
            reminder.id.hashCode(), 
            intent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Mark Done action
        val markDoneIntent = Intent(context, ReminderActionReceiver::class.java).apply {
            action = ACTION_MARK_DONE
            putExtra(EXTRA_REMINDER_ID, reminder.id)
        }
        val markDonePendingIntent = PendingIntent.getBroadcast(
            context, 
            reminder.id.hashCode() + 1, 
            markDoneIntent, 
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(reminder.title)
            .setContentText(reminder.note.ifBlank { context.getString(R.string.relationship_reminders) })
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                R.mipmap.ic_launcher, 
                context.getString(R.string.reminder_notification_mark_done), 
                markDonePendingIntent
            )

        notificationManager.notify(reminder.id.hashCode(), builder.build())
    }

    fun cancelNotification(reminderId: String) {
        notificationManager.cancel(reminderId.hashCode())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_REMINDERS,
                context.getString(R.string.reminder_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.reminder_channel_description)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}
