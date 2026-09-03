package com.akash.kontactplus.feature.relationship.domain.usecase

import com.akash.kontactplus.feature.relationship.domain.repository.RelationshipRepository
import javax.inject.Inject

class CompleteRelationshipReminderUseCase @Inject constructor(
    private val repository: RelationshipRepository
) {
    suspend operator fun invoke(reminderId: String): Result<Unit> {
        return repository.completeReminder(reminderId)
    }
}
