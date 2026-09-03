package com.akash.kontactplus.feature.relationship.domain.usecase

import com.akash.kontactplus.feature.relationship.domain.model.RelationshipReminder
import com.akash.kontactplus.feature.relationship.domain.repository.RelationshipRepository
import com.akash.kontactplus.feature.relationship.domain.scheduler.RelationshipReminderScheduler
import java.time.Instant
import javax.inject.Inject

class ScheduleRelationshipReminderUseCase @Inject constructor(
    private val repository: RelationshipRepository,
    private val scheduler: RelationshipReminderScheduler
) {
    suspend operator fun invoke(reminder: RelationshipReminder): Result<Unit> {
        if (reminder.scheduledAt.isBefore(Instant.now())) {
            return Result.failure(Exception("Scheduled time is in the past"))
        }

        // Save to DB first
        repository.scheduleReminder(reminder).onSuccess {
            // Then schedule work
            scheduler.schedule(reminder.id, reminder.scheduledAt.toEpochMilli()).onSuccess { workId ->
                repository.updateReminderWorkId(reminder.id, workId)
            }
        }
        return Result.success(Unit)
    }
}
