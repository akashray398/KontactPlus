package com.akash.kontactplus.feature.relationship.domain.usecase

import com.akash.kontactplus.feature.relationship.domain.repository.ConnectionInsightsRepository
import javax.inject.Inject

class AcceptCallHistoryDisclosureUseCase @Inject constructor(
    private val repository: ConnectionInsightsRepository
) {
    suspend operator fun invoke() {
        repository.setCallHistoryDisclosureAccepted(true)
        repository.setCallHistoryInsightsEnabled(true)
    }
}
