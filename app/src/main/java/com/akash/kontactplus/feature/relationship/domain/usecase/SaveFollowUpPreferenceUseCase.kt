package com.akash.kontactplus.feature.relationship.domain.usecase

import com.akash.kontactplus.feature.relationship.domain.model.ContactFollowUpPreference
import com.akash.kontactplus.feature.relationship.domain.repository.RelationshipRepository
import javax.inject.Inject

class SaveFollowUpPreferenceUseCase @Inject constructor(
    private val repository: RelationshipRepository
) {
    suspend operator fun invoke(preference: ContactFollowUpPreference): Result<Unit> {
        return repository.saveFollowUpPreference(preference)
    }
}
