package com.akash.kontactplus.feature.relationship.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.akash.kontactplus.feature.relationship.domain.repository.RelationshipRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: RelationshipRepository

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getStringExtra(ReminderNotificationManager.EXTRA_REMINDER_ID) ?: return
        
        if (intent.action == ReminderNotificationManager.ACTION_MARK_DONE) {
            CoroutineScope(Dispatchers.IO).launch {
                repository.completeReminder(reminderId)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            manager.cancel(reminderId.hashCode())
        }
    }
}
