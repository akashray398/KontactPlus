package com.akash.kontactplus.feature.relationship.domain.usecase

import com.akash.kontactplus.feature.relationship.domain.repository.RelationshipRepository
import javax.inject.Inject

class SavePrivateNoteUseCase @Inject constructor(
    private val repository: RelationshipRepository
) {
    suspend operator fun invoke(lookupKey: String, note: String): Result<Unit> {
        if (note.length > 5000) return Result.failure(Exception("Note is too long"))
        return repository.savePrivateNote(lookupKey, note)
    }
}
