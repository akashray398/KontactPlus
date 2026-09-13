package com.akash.kontactplus.feature.relationship.domain.usecase

import androidx.work.WorkManager
import com.akash.kontactplus.feature.relationship.domain.repository.RelationshipRepository
import javax.inject.Inject

class DeleteAllRelationshipDataUseCase @Inject constructor(
    private val repository: RelationshipRepository,
    private val workManager: WorkManager
) {
    suspend operator fun invoke(): Result<Unit> {
        // Cancel all relationship reminders before deleting data
        workManager.cancelAllWorkByTag("relationship_reminder")
        return repository.deleteAllRelationshipData()
    }
}
