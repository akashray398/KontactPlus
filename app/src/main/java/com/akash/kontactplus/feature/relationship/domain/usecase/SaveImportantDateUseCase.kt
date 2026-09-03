package com.akash.kontactplus.feature.relationship.domain.usecase

import com.akash.kontactplus.feature.relationship.domain.model.ImportantDate
import com.akash.kontactplus.feature.relationship.domain.repository.RelationshipRepository
import javax.inject.Inject

class SaveImportantDateUseCase @Inject constructor(
    private val repository: RelationshipRepository
) {
    suspend operator fun invoke(importantDate: ImportantDate): Result<Unit> {
        if (importantDate.title.isBlank()) return Result.failure(Exception("Title cannot be blank"))
        return repository.saveImportantDate(importantDate)
    }
}
