package com.akash.kontactplus.feature.relationship.domain.usecase

import com.akash.kontactplus.feature.contacts.domain.repository.ContactsRepository
import com.akash.kontactplus.feature.relationship.domain.model.*
import com.akash.kontactplus.feature.relationship.domain.repository.RelationshipRepository
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class ObserveAssistantDashboardUseCase @Inject constructor(
    private val relationshipRepository: RelationshipRepository,
    private val contactsRepository: ContactsRepository
) {
    operator fun invoke(): Flow<AssistantDashboard> {
        val contactsFlow = flow {
            emit(contactsRepository.getContacts().getOrDefault(emptyList()))
        }.onStart { emit(emptyList()) }

        return combine(
            relationshipRepository.observeAllUpcomingDates().onStart { emit(emptyList()) },
            relationshipRepository.observeScheduledReminders().onStart { emit(emptyList()) },
            relationshipRepository.observeOverdueReminders().onStart { emit(emptyList()) },
            contactsFlow
        ) { dates, scheduled, overdue, allContacts ->
            val contactMap = allContacts.associateBy { it.lookupKey }
            val today = LocalDate.now()
            
            AssistantDashboard(
                dueToday = scheduled.filter { 
                    it.scheduledAt.atZone(ZoneId.systemDefault()).toLocalDate() == today
                }.map { it.toDashboardItem(contactMap[it.lookupKey]?.displayName) },
                
                overdue = overdue.map { it.toDashboardItem(contactMap[it.lookupKey]?.displayName) },
                
                upcomingDates = dates.filter { 
                    it.localDate.isAfter(today) && it.localDate.isBefore(today.plusDays(30)) 
                }.map { it.toDashboardItem(contactMap[it.lookupKey]?.displayName) },
                
                upcomingReminders = scheduled.filter { 
                    it.scheduledAt.atZone(ZoneId.systemDefault()).toLocalDate().isAfter(today)
                }.map { it.toDashboardItem(contactMap[it.lookupKey]?.displayName) }
            )
        }
    }

    private fun RelationshipReminder.toDashboardItem(contactName: String?) = DashboardItem(
        id = id,
        lookupKey = lookupKey,
        contactName = contactName ?: "Unknown Contact",
        title = title,
        subtitle = note,
        type = DashboardItemType.Reminder
    )

    private fun ImportantDate.toDashboardItem(contactName: String?) = DashboardItem(
        id = id.toString(),
        lookupKey = lookupKey,
        contactName = contactName ?: "Unknown Contact",
        title = title,
        subtitle = localDate.toString(),
        type = DashboardItemType.Date
    )
}

data class AssistantDashboard(
    val dueToday: List<DashboardItem> = emptyList(),
    val overdue: List<DashboardItem> = emptyList(),
    val upcomingDates: List<DashboardItem> = emptyList(),
    val upcomingReminders: List<DashboardItem> = emptyList()
)

data class DashboardItem(
    val id: String,
    val lookupKey: String,
    val contactName: String,
    val title: String,
    val subtitle: String?,
    val type: DashboardItemType
)

enum class DashboardItemType { Reminder, Date }
