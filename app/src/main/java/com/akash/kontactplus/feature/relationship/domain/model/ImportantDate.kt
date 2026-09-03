package com.akash.kontactplus.feature.relationship.domain.model

import java.time.LocalDate

data class ImportantDate(
    val id: Long,
    val lookupKey: String,
    val title: String,
    val localDate: LocalDate,
    val type: ImportantDateType,
    val repeatsYearly: Boolean
)

enum class ImportantDateType {
    Birthday,
    Anniversary,
    Custom
}
