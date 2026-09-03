package com.akash.kontactplus.feature.relationship.domain.scheduler

interface RelationshipReminderScheduler {
    suspend fun schedule(reminderId: String, scheduledAtEpochMillis: Long): Result<String>
    suspend fun cancel(workRequestId: String): Result<Unit>
}
