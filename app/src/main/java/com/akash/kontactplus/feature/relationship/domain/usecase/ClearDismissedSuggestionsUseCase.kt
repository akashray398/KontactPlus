package com.akash.kontactplus.feature.relationship.domain.usecase

import com.akash.kontactplus.feature.relationship.domain.repository.RelationshipRepository
import javax.inject.Inject

class ClearDismissedSuggestionsUseCase @Inject constructor(
    private val repository: RelationshipRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.clearAllSuggestionActions()
    }
}
