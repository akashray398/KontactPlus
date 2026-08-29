package com.akash.kontactplus.feature.recents.presentation

import androidx.annotation.StringRes
import com.akash.kontactplus.feature.recents.domain.model.RecentCall

/**
 * Immutable UI state for the Recents screen.
 */
data class RecentsUiState(
    val accessState: RecentsAccessState = RecentsAccessState.CheckingRole,
    val visibleCalls: List<RecentCall> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
    val hasLoadedCalls: Boolean = false
)
