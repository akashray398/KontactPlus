package com.akash.kontactplus.feature.ai.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akash.kontactplus.feature.ai.domain.model.*
import com.akash.kontactplus.feature.ai.domain.repository.AiRepository
import com.akash.kontactplus.feature.ai.domain.usecase.GenerateAiTextUseCase
import com.akash.kontactplus.feature.ai.domain.usecase.LocalMessageTemplateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiViewModel @Inject constructor(
    private val repository: AiRepository,
    private val generateAiTextUseCase: GenerateAiTextUseCase,
    private val localTemplateUseCase: LocalMessageTemplateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiUiState())
    val uiState: StateFlow<AiUiState> = _uiState.asStateFlow()

    init {
        repository.hasAcceptedDisclosure()
            .onEach { accepted -> _uiState.update { it.copy(hasAcceptedDisclosure = accepted) } }
            .launchIn(viewModelScope)
    }

    fun onActionSelected(actionType: AiActionType) {
        _uiState.update { it.copy(selectedAction = actionType, step = AiFlowStep.Configure) }
    }

    fun onToneSelected(tone: AiTone) {
        _uiState.update { it.copy(selectedTone = tone) }
    }

    fun onInstructionChanged(instruction: String) {
        _uiState.update { it.copy(instructionInput = instruction) }
    }

    fun onAcceptDisclosure() {
        viewModelScope.launch {
            repository.setAcceptedDisclosure(true)
        }
    }

    fun onPreviewPayload() {
        val context = AiDraftContext(
            actionType = _uiState.value.selectedAction ?: return,
            tone = _uiState.value.selectedTone,
            userInstruction = _uiState.value.instructionInput,
            contactAlias = if (_uiState.value.includeContactName) _uiState.value.contactName else "Contact",
            selectedText = if (_uiState.value.includeSelectedText) _uiState.value.selectedText else "",
            relationshipContext = if (_uiState.value.includeRelationshipContext) _uiState.value.relationshipContext else null
        )
        _uiState.update { it.copy(draftContext = context, step = AiFlowStep.Preview) }
    }

    fun onGenerate() {
        val context = _uiState.value.draftContext ?: return
        
        _uiState.update { it.copy(isGenerating = true) }
        
        viewModelScope.launch {
            val result = generateAiTextUseCase(context)
            _uiState.update { 
                it.copy(
                    isGenerating = false,
                    generationResult = result,
                    step = AiFlowStep.Result
                ) 
            }
        }
    }

    fun onUseLocalTemplate() {
        val context = _uiState.value.draftContext ?: return
        val text = localTemplateUseCase(context)
        _uiState.update { 
            it.copy(
                generationResult = AiGenerationResult.Success(text, "Local Template"),
                step = AiFlowStep.Result
            )
        }
    }

    fun reset() {
        _uiState.update { AiUiState(hasAcceptedDisclosure = it.hasAcceptedDisclosure) }
    }
}

data class AiUiState(
    val step: AiFlowStep = AiFlowStep.ActionPicker,
    val hasAcceptedDisclosure: Boolean = false,
    val selectedAction: AiActionType? = null,
    val selectedTone: AiTone = AiTone.Friendly,
    val instructionInput: String = "",
    val contactName: String = "Contact",
    val selectedText: String = "",
    val relationshipContext: String? = null,
    val includeContactName: Boolean = false,
    val includeSelectedText: Boolean = false,
    val includeRelationshipContext: Boolean = false,
    val draftContext: AiDraftContext? = null,
    val isGenerating: Boolean = false,
    val generationResult: AiGenerationResult? = null
)

enum class AiFlowStep {
    ActionPicker,
    Configure,
    Preview,
    Result
}
