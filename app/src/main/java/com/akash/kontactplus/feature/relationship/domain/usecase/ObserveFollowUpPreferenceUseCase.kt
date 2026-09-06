package com.akash.kontactplus.feature.relationship.domain.usecase

import com.akash.kontactplus.feature.relationship.domain.model.ContactFollowUpPreference
import com.akash.kontactplus.feature.relationship.domain.repository.RelationshipRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFollowUpPreferenceUseCase @Inject constructor(
    private val repository: RelationshipRepository
) {
    operator fun invoke(lookupKey: String): Flow<ContactFollowUpPreference?> {
        return repository.observeFollowUpPreference(lookupKey)
    }
}
