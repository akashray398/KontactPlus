package com.akash.kontactplus.feature.relationship.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.kontactplus.feature.contacts.domain.usecase.GetContactUseCase
import com.akash.kontactplus.feature.relationship.domain.model.*
import com.akash.kontactplus.feature.relationship.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ContactRelationshipViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getContactUseCase: GetContactUseCase,
    private val observeContactRelationshipUseCase: ObserveContactRelationshipUseCase,
    private val savePrivateNoteUseCase: SavePrivateNoteUseCase,
    private val scheduleRelationshipReminderUseCase: ScheduleRelationshipReminderUseCase,
    private val createRelationshipTagUseCase: CreateRelationshipTagUseCase,
    private val assignTagToContactUseCase: AssignTagToContactUseCase,
    private val removeTagFromContactUseCase: RemoveTagFromContactUseCase,
    private val saveImportantDateUseCase: SaveImportantDateUseCase,
    private val deleteImportantDateUseCase: DeleteImportantDateUseCase,
    private val completeRelationshipReminderUseCase: CompleteRelationshipReminderUseCase,
    private val cancelRelationshipReminderUseCase: CancelRelationshipReminderUseCase
) : ViewModel() {

    private val lookupKey: String = checkNotNull(savedStateHandle[KEY_LOOKUP_KEY])
    
    private val _uiState = MutableStateFlow(ContactRelationshipUiState())
    val uiState: StateFlow<ContactRelationshipUiState> = _uiState.asStateFlow()

    init {
        loadContact()
        observeRelationship()
    }

    private fun loadContact() {
        viewModelScope.launch {
            getContactUseCase(lookupKey).onSuccess { contact ->
                _uiState.update { it.copy(contact = contact) }
            }
        }
    }

    private fun observeRelationship() {
        observeContactRelationshipUseCase(lookupKey)
            .onEach { relationship ->
                _uiState.update { 
                    it.copy(
                        relationship = relationship ?: ContactRelationship(lookupKey),
                        noteInput = relationship?.privateNote ?: it.noteInput
                    ) 
                }
            }
            .launchIn(viewModelScope)
    }

    fun onNoteChanged(note: String) {
        _uiState.update { it.copy(noteInput = note, hasUnsavedChanges = true) }
    }

    fun saveNote() {
        viewModelScope.launch {
            savePrivateNoteUseCase(lookupKey, _uiState.value.noteInput).onSuccess {
                _uiState.update { it.copy(hasUnsavedChanges = false) }
            }
        }
    }

    fun addTag(name: String, colorKey: String) {
        viewModelScope.launch {
            createRelationshipTagUseCase(name, colorKey).onSuccess { tagId ->
                assignTagToContactUseCase(lookupKey, tagId)
            }
        }
    }

    fun removeTag(tagId: Long) {
        viewModelScope.launch {
            removeTagFromContactUseCase(lookupKey, tagId)
        }
    }

    fun addImportantDate(title: String, date: LocalDate, type: ImportantDateType, repeatsYearly: Boolean) {
        viewModelScope.launch {
            val importantDate = ImportantDate(
                id = 0,
                lookupKey = lookupKey,
                title = title,
                localDate = date,
                type = type,
                repeatsYearly = repeatsYearly
            )
            saveImportantDateUseCase(importantDate)
        }
    }

    fun deleteImportantDate(id: Long) {
        viewModelScope.launch {
            deleteImportantDateUseCase(id)
        }
    }

    fun addReminder(title: String, note: String, scheduledAt: Instant) {
        viewModelScope.launch {
            val reminder = RelationshipReminder(
                id = UUID.randomUUID().toString(),
                lookupKey = lookupKey,
                title = title,
                note = note,
                scheduledAt = scheduledAt,
                status = ReminderStatus.Scheduled
            )
            scheduleRelationshipReminderUseCase(reminder)
        }
    }

    fun completeReminder(id: String) {
        viewModelScope.launch {
            completeRelationshipReminderUseCase(id)
        }
    }

    fun cancelReminder(id: String) {
        viewModelScope.launch {
            cancelRelationshipReminderUseCase(id)
        }
    }

    companion object {
        const val KEY_LOOKUP_KEY = "lookupKey"
    }
}

data class ContactRelationshipUiState(
    val contact: com.akash.kontactplus.feature.contacts.domain.model.Contact? = null,
    val relationship: ContactRelationship = ContactRelationship(""),
    val noteInput: String = "",
    val hasUnsavedChanges: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessageRes: Int? = null
)
