package com.akash.kontactplus.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.kontactplus.feature.contacts.domain.repository.ContactsRepository
import com.akash.kontactplus.feature.relationship.domain.repository.RelationshipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val contactsRepository: ContactsRepository,
    private val relationshipRepository: RelationshipRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserProfileUiState(isLoading = true))
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    fun updateNameAndPhone(name: String, phone: String) {
        _uiState.update { 
            it.copy(
                userName = name.ifBlank { "My Profile" },
                phoneNumber = phone.ifBlank { "+91 98765 43210" }
            ) 
        }
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            contactsRepository.getContacts().fold(
                onSuccess = { contacts ->
                    val userContact = contacts.firstOrNull()
                    _uiState.update { 
                        it.copy(
                            userName = userContact?.displayName?.ifBlank { "My Profile" } ?: "My Profile",
                            phoneNumber = userContact?.phoneNumbers?.firstOrNull() ?: "+91 98765 43210",
                            totalContacts = contacts.size,
                            isLoading = false
                        ) 
                    }
                },
                onFailure = {
                    _uiState.update { it.copy(isLoading = false) }
                }
            )
        }

        viewModelScope.launch {
            relationshipRepository.observeAllFacts().collectLatest { facts ->
                _uiState.update { it.copy(memoryFactsCount = facts.size) }
            }
        }

        viewModelScope.launch {
            relationshipRepository.observeScheduledReminders().collectLatest { reminders ->
                _uiState.update { it.copy(activeFollowUps = reminders.size) }
            }
        }
    }
}
