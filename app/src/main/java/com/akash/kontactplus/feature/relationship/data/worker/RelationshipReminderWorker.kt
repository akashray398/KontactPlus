package com.akash.kontactplus.feature.relationship.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.akash.kontactplus.feature.relationship.data.notification.ReminderNotificationManager
import com.akash.kontactplus.feature.relationship.domain.model.ReminderStatus
import com.akash.kontactplus.feature.relationship.domain.repository.RelationshipRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class RelationshipReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: RelationshipRepository,
    private val notificationManager: ReminderNotificationManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val reminderId = inputData.getString(KEY_REMINDER_ID) ?: return Result.success()
        
        // Load relationship data to find the reminder
        // Note: Currently we only observe by lookupKey. 
        // We might need a direct "getReminder(id)" in repository.
        // Let's assume we can get it or we add a helper to repository.
        
        // For simplicity in Step 12, I'll assume we can get all scheduled reminders and find the one.
        val reminders = repository.observeScheduledReminders().firstOrNull() ?: emptyList()
        val reminder = reminders.find { it.id == reminderId } ?: return Result.success()
        
        if (reminder.status == ReminderStatus.Scheduled) {
            notificationManager.showReminderNotification(reminder)
        }
        
        return Result.success()
    }

    companion object {
        const val KEY_REMINDER_ID = "reminder_id"
    }
}
