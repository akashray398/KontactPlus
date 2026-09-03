package com.akash.kontactplus.core.telecom.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import com.akash.kontactplus.R
import com.akash.kontactplus.core.telecom.ActiveCallInfo
import com.akash.kontactplus.core.telecom.ActiveCallState
import com.akash.kontactplus.feature.telecom.ActiveCallActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        private const val CHANNEL_ID_INCOMING = "incoming_calls"
        private const val CHANNEL_ID_ONGOING = "ongoing_calls"
        private const val NOTIFICATION_ID = 1001
        
        const val ACTION_ANSWER = "com.akash.kontactplus.ACTION_ANSWER"
        const val ACTION_DECLINE = "com.akash.kontactplus.ACTION_DECLINE"
        const val ACTION_HANG_UP = "com.akash.kontactplus.ACTION_HANG_UP"
    }

    init {
        createNotificationChannels()
    }

    fun showIncomingCallNotification(callInfo: ActiveCallInfo) {
        val person = Person.Builder()
            .setName(callInfo.displayName.ifBlank { callInfo.phoneNumber.ifBlank { context.getString(R.string.recents_unknown_caller) } })
            .setImportant(true)
            .build()

        val fullScreenIntent = Intent(context, ActiveCallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 0, fullScreenIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_INCOMING)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setOngoing(true)
            .setAutoCancel(false)
            .addPerson(person.uri)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val answerIntent = createActionIntent(ACTION_ANSWER)
            val declineIntent = createActionIntent(ACTION_DECLINE)
            
            builder.setStyle(
                NotificationCompat.CallStyle.forIncomingCall(
                    person, declineIntent, answerIntent
                )
            )
        } else {
            builder.addAction(R.mipmap.ic_launcher, context.getString(R.string.active_call_answer), createActionIntent(ACTION_ANSWER))
            builder.addAction(R.mipmap.ic_launcher, context.getString(R.string.active_call_decline), createActionIntent(ACTION_DECLINE))
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    fun showOngoingCallNotification(callInfo: ActiveCallInfo) {
        val person = Person.Builder()
            .setName(callInfo.displayName.ifBlank { callInfo.phoneNumber.ifBlank { context.getString(R.string.recents_unknown_caller) } })
            .build()

        val contentIntent = Intent(context, ActiveCallActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            context, 1, contentIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_ONGOING)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setOngoing(true)
            .setContentIntent(contentPendingIntent)
            .addPerson(person.uri)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val hangUpIntent = createActionIntent(ACTION_HANG_UP)
            builder.setStyle(
                NotificationCompat.CallStyle.forOngoingCall(
                    person, hangUpIntent
                )
            )
        } else {
            builder.addAction(R.mipmap.ic_launcher, context.getString(R.string.active_call_end), createActionIntent(ACTION_HANG_UP))
            builder.setContentTitle(person.name)
            builder.setContentText(context.getString(R.string.active_call_active))
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val incomingChannel = NotificationChannel(
                CHANNEL_ID_INCOMING,
                context.getString(R.string.notification_channel_incoming_calls),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notification_channel_incoming_calls_description)
                setSound(null, null)
                enableVibration(true)
            }

            val ongoingChannel = NotificationChannel(
                CHANNEL_ID_ONGOING,
                context.getString(R.string.notification_channel_ongoing_calls),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.notification_channel_ongoing_calls_description)
                setSound(null, null)
                enableVibration(false)
            }

            notificationManager.createNotificationChannels(listOf(incomingChannel, ongoingChannel))
        }
    }

    private fun createActionIntent(action: String): PendingIntent {
        val intent = Intent(context, CallActionReceiver::class.java).apply {
            this.action = action
        }
        return PendingIntent.getBroadcast(
            context, action.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE
        )
    }
}
