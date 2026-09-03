package com.akash.kontactplus.feature.relationship.domain.usecase

import com.akash.kontactplus.feature.relationship.domain.repository.RelationshipRepository
import javax.inject.Inject

class CreateRelationshipTagUseCase @Inject constructor(
    private val repository: RelationshipRepository
) {
    suspend operator fun invoke(name: String, colorKey: String): Result<Long> {
        if (name.isBlank()) return Result.failure(Exception("Tag name cannot be blank"))
        return repository.createTag(name, colorKey)
    }
}
