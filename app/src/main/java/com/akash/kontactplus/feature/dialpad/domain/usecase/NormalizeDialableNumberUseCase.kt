package com.akash.kontactplus.feature.dialpad.domain.usecase

import javax.inject.Inject

/**
 * Normalizes a string into a dialable phone number.
 */
class NormalizeDialableNumberUseCase @Inject constructor() {
    operator fun invoke(input: String): String {
        if (input.isBlank()) return ""
        
        val normalized = StringBuilder()
        input.forEachIndexed { index, c ->
            if (c.isDigit() || c == '*' || c == '#') {
                normalized.append(c)
            } else if (c == '+' && index == 0) {
                normalized.append(c)
            }
        }
        return normalized.toString()
    }
}
