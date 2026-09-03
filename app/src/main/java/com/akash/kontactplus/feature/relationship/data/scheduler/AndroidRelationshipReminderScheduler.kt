package com.akash.kontactplus.feature.relationship.data.scheduler

import android.content.Context
import androidx.work.*
import com.akash.kontactplus.feature.relationship.data.worker.RelationshipReminderWorker
import com.akash.kontactplus.feature.relationship.domain.scheduler.RelationshipReminderScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AndroidRelationshipReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : RelationshipReminderScheduler {

    private val workManager = WorkManager.getInstance(context)

    override suspend fun schedule(reminderId: String, scheduledAtEpochMillis: Long): Result<String> = runCatching {
        val delay = scheduledAtEpochMillis - System.currentTimeMillis()
        
        val inputData = Data.Builder()
            .putString(RelationshipReminderWorker.KEY_REMINDER_ID, reminderId)
            .build()
        
        val workRequest = OneTimeWorkRequestBuilder<RelationshipReminderWorker>()
            .setInitialDelay(maxOf(0, delay), TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("reminder_$reminderId")
            .build()

        workManager.enqueueUniqueWork(
            "reminder_$reminderId",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        
        workRequest.id.toString()
    }

    override suspend fun cancel(workRequestId: String): Result<Unit> = runCatching {
        workManager.cancelWorkById(UUID.fromString(workRequestId))
    }
}
