package com.akash.kontactplus.feature.relationship.domain.usecase

import com.akash.kontactplus.feature.relationship.domain.repository.RelationshipRepository
import javax.inject.Inject

class RemoveTagFromContactUseCase @Inject constructor(
    private val repository: RelationshipRepository
) {
    suspend operator fun invoke(lookupKey: String, tagId: Long): Result<Unit> {
        return repository.removeTagFromContact(lookupKey, tagId)
    }
}
