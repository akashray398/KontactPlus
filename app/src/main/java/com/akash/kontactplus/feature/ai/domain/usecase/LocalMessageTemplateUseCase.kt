package com.akash.kontactplus.feature.ai.domain.usecase

import com.akash.kontactplus.feature.ai.domain.model.AiActionType
import com.akash.kontactplus.feature.ai.domain.model.AiDraftContext
import javax.inject.Inject

class LocalMessageTemplateUseCase @Inject constructor() {
    
    operator fun invoke(context: AiDraftContext): String {
        return when (context.actionType) {
            AiActionType.FollowUpDraft -> "Hi! It was great speaking with you. I wanted to follow up about our recent conversation."
            AiActionType.ImportantDateGreeting -> "Wishing you a wonderful day! Hope you have a great time celebrating."
            AiActionType.ConversationStarters -> "How have things been going recently? Would love to catch up soon."
            AiActionType.RewriteTone -> context.selectedText // Can't really rewrite locally, just return original
            AiActionType.SummarizeSelectedNote -> context.selectedText // Can't summarize locally
        }
    }
}
