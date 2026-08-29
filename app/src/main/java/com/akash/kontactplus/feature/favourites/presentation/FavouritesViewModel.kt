package com.akash.kontactplus.feature.favourites.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.kontactplus.R
import com.akash.kontactplus.feature.favourites.domain.usecase.ObserveFavouriteContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val observeFavouriteContactsUseCase: ObserveFavouriteContactsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavouritesUiState())
    val uiState: StateFlow<FavouritesUiState> = _uiState.asStateFlow()

    init {
        startObservingFavourites()
    }

    fun onPermissionStatusChanged(hasPermission: Boolean) {
        _uiState.update { it.copy(hasContactsPermission = hasPermission) }
        if (hasPermission) {
            startObservingFavourites()
        } else {
            _uiState.update { it.copy(contacts = emptyList(), isLoading = false) }
        }
    }

    fun retry() {
        startObservingFavourites()
    }

    private fun startObservingFavourites() {
        if (!_uiState.value.hasContactsPermission) return

        viewModelScope.launch {
            observeFavouriteContactsUseCase()
                .onStart { _uiState.update { it.copy(isLoading = true, errorMessageRes = null) } }
                .catch {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessageRes = R.string.favourites_error_description 
                        ) 
                    }
                }
                .collectLatest { favourites ->
                    _uiState.update { 
                        it.copy(
                            contacts = favourites, 
                            isLoading = false,
                            errorMessageRes = null
                        ) 
                    }
                }
        }
    }
}
