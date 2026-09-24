package com.akash.kontactplus.feature.contacts.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.kontactplus.R
import com.akash.kontactplus.feature.ai.domain.usecase.AnalyzeConversationUseCase
import com.akash.kontactplus.feature.contacts.domain.usecase.GetContactUseCase
import com.akash.kontactplus.feature.favourites.domain.usecase.IsContactFavouriteUseCase
import com.akash.kontactplus.feature.favourites.domain.usecase.ToggleFavouriteContactUseCase
import com.akash.kontactplus.feature.relationship.domain.model.ContactFact
import com.akash.kontactplus.feature.relationship.domain.model.FactCategory
import com.akash.kontactplus.feature.relationship.domain.repository.RelationshipRepository
import com.akash.kontactplus.feature.relationship.domain.usecase.GetMemoryReplayUseCase
import com.akash.kontactplus.feature.relationship.domain.usecase.GetRelationshipTimelineUseCase
import com.akash.kontactplus.feature.relationship.domain.usecase.ObserveContactRelationshipUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getContactUseCase: GetContactUseCase,
    private val isContactFavouriteUseCase: IsContactFavouriteUseCase,
    private val toggleFavouriteContactUseCase: ToggleFavouriteContactUseCase,
    private val observeContactRelationshipUseCase: ObserveContactRelationshipUseCase,
    private val getMemoryReplayUseCase: GetMemoryReplayUseCase,
    private val getRelationshipTimelineUseCase: GetRelationshipTimelineUseCase,
    private val analyzeConversationUseCase: AnalyzeConversationUseCase,
    private val relationshipRepository: RelationshipRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ContactDetailsUiState>(ContactDetailsUiState.Loading)
    val uiState: StateFlow<ContactDetailsUiState> = _uiState.asStateFlow()

    private val lookupKey: String? = savedStateHandle[KEY_LOOKUP_KEY]

    init {
        loadContact()
        observeFavouriteStatus()
        observeRelationship()
    }

    fun retry() {
        loadContact()
    }

    fun onFavouriteClick() {
        val currentState = _uiState.value
        if (currentState is ContactDetailsUiState.Success && !currentState.isFavouriteActionInProgress) {
            val key = currentState.contact.lookupKey
            val isFavourite = currentState.isFavourite
            
            viewModelScope.launch {
                _uiState.update { 
                    if (it is ContactDetailsUiState.Success) it.copy(isFavouriteActionInProgress = true) else it 
                }
                
                toggleFavouriteContactUseCase(key, isFavourite).fold(
                    onSuccess = {
                        // Room observation will update the state
                    },
                    onFailure = {
                        _uiState.update { 
                            if (it is ContactDetailsUiState.Success) {
                                it.copy(
                                    isFavouriteActionInProgress = false,
                                    favouriteActionErrorRes = R.string.favourite_update_error
                                )
                            } else it 
                        }
                    }
                )
            }
        }
    }

    fun onAddFact(factText: String, category: FactCategory) {
        val key = lookupKey ?: return
        if (factText.isBlank()) return
        viewModelScope.launch {
            relationshipRepository.saveFact(
                ContactFact(
                    lookupKey = key,
                    fact = factText.trim(),
                    category = category
                )
            )
        }
    }

    fun onDeleteFact(factId: Long) {
        viewModelScope.launch {
            relationshipRepository.deleteFact(factId)
        }
    }

    fun onAnalyzeConversation(notes: String) {
        val currentState = _uiState.value as? ContactDetailsUiState.Success ?: return
        val key = lookupKey ?: return

        viewModelScope.launch {
            _uiState.update { (it as? ContactDetailsUiState.Success)?.copy(isAnalyzingConversation = true) ?: it }
            analyzeConversationUseCase(
                lookupKey = key,
                contactName = currentState.contact.displayName,
                rawNotes = notes
            ).fold(
                onSuccess = { result ->
                    _uiState.update { 
                        (it as? ContactDetailsUiState.Success)?.copy(
                            isAnalyzingConversation = false,
                            analysisResult = result
                        ) ?: it
                    }
                },
                onFailure = {
                    _uiState.update { 
                        (it as? ContactDetailsUiState.Success)?.copy(isAnalyzingConversation = false) ?: it
                    }
                }
            )
        }
    }

    private fun loadContact() {
        val key = lookupKey
        if (key.isNullOrBlank()) {
            _uiState.value = ContactDetailsUiState.Error(R.string.contact_details_error_description)
            return
        }

        viewModelScope.launch {
            _uiState.value = ContactDetailsUiState.Loading
            getContactUseCase(key).fold(
                onSuccess = { contact ->
                    if (contact != null) {
                        _uiState.value = ContactDetailsUiState.Success(contact = contact)
                        observeMemoryReplay(key, contact.displayName)
                        observeTimeline(key, contact.phoneNumbers)
                    } else {
                        _uiState.value = ContactDetailsUiState.NotFound
                    }
                },
                onFailure = {
                    _uiState.value = ContactDetailsUiState.Error(R.string.contact_details_error_description)
                }
            )
        }
    }

    private fun observeMemoryReplay(key: String, contactName: String) {
        viewModelScope.launch {
            getMemoryReplayUseCase(key, contactName).collectLatest { briefing ->
                _uiState.update { state ->
                    if (state is ContactDetailsUiState.Success) {
                        state.copy(
                            memoryReplay = briefing,
                            health = briefing.health,
                            facts = briefing.keyFacts
                        )
                    } else state
                }
            }
        }
    }

    private fun observeTimeline(key: String, phoneNumbers: List<String>) {
        viewModelScope.launch {
            getRelationshipTimelineUseCase(key, phoneNumbers).collectLatest { timelineItems ->
                _uiState.update { state ->
                    if (state is ContactDetailsUiState.Success) {
                        state.copy(timeline = timelineItems)
                    } else state
                }
            }
        }
    }

    private fun observeFavouriteStatus() {
        val key = lookupKey ?: return
        viewModelScope.launch {
            isContactFavouriteUseCase(key).collectLatest { isFavourite ->
                _uiState.update { state ->
                    if (state is ContactDetailsUiState.Success) {
                        state.copy(
                            isFavourite = isFavourite,
                            isFavouriteActionInProgress = false
                        )
                    } else state
                }
            }
        }
    }

    private fun observeRelationship() {
        val key = lookupKey ?: return
        viewModelScope.launch {
            observeContactRelationshipUseCase(key).collectLatest { relationship ->
                _uiState.update { state ->
                    if (state is ContactDetailsUiState.Success) {
                        state.copy(relationship = relationship)
                    } else state
                }
            }
        }
    }

    companion object {
        const val KEY_LOOKUP_KEY = "lookupKey"
    }
}
