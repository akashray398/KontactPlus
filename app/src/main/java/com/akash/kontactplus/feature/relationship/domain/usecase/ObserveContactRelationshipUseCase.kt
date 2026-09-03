package com.akash.kontactplus.feature.relationship.domain.usecase

import com.akash.kontactplus.feature.relationship.domain.model.ContactRelationship
import com.akash.kontactplus.feature.relationship.domain.repository.RelationshipRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveContactRelationshipUseCase @Inject constructor(
    private val repository: RelationshipRepository
) {
    operator fun invoke(lookupKey: String): Flow<ContactRelationship?> {
        return repository.observeRelationship(lookupKey)
    }
}
