package com.akash.kontactplus.feature.ai.domain.usecase

import com.akash.kontactplus.feature.ai.domain.model.AiDraftContext
import javax.inject.Inject

class PrepareAiRequestUseCase @Inject constructor() {
    
    operator fun invoke(context: AiDraftContext): AiDraftContext {
        return context.copy(
            userInstruction = redact(context.userInstruction.take(500)),
            selectedText = redact(context.selectedText.take(1500)),
            relationshipContext = context.relationshipContext?.let { redact(it.take(300)) },
            contactAlias = redact(context.contactAlias)
        )
    }

    private fun redact(text: String): String {
        // Redact phone numbers (simple pattern)
        val phonePattern = """\b(?:\+?\d{1,3}[-.\s]?)?\(?\d{3}\)?[-.\s]?\d{3}[-.\s]?\d{4}\b""".toRegex()
        // Redact emails
        val emailPattern = """\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}\b""".toRegex()
        
        return text
            .replace(phonePattern, "[PHONE]")
            .replace(emailPattern, "[EMAIL]")
    }
}
