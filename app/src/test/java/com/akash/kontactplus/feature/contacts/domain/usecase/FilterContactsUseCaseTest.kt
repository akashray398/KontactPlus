package com.akash.kontactplus.feature.contacts.domain.usecase

import com.akash.kontactplus.feature.contacts.domain.model.Contact
import com.akash.kontactplus.feature.contacts.domain.model.ContactSortOrder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FilterContactsUseCaseTest {

    private val filterUseCase = FilterContactsUseCase()
    private val contacts = listOf(
        Contact(1, "k1", "Akash Patel", listOf("123-456", "987-654")),
        Contact(2, "k2", "Jane Doe", listOf("+1 (555) 123-4567")),
        Contact(3, "k3", "", listOf("1112223333")),
        Contact(4, "k4", "Zack", listOf("444555"))
    )

    @Test
    fun `blank query returns all contacts in ascending order`() {
        val result = filterUseCase(contacts, "", ContactSortOrder.NameAscending)
        assertEquals(4, result.size)
        assertEquals("Akash Patel", result[0].displayName)
        assertEquals("Jane Doe", result[1].displayName)
        assertEquals("Zack", result[2].displayName)
        assertEquals("", result[3].displayName)
    }

    @Test
    fun `name search is case-insensitive`() {
        val result = filterUseCase(contacts, "akash", ContactSortOrder.NameAscending)
        assertEquals(1, result.size)
        assertEquals("Akash Patel", result[0].displayName)
    }

    @Test
    fun `phone search ignores visual separators`() {
        val result = filterUseCase(contacts, "5551234567", ContactSortOrder.NameAscending)
        assertEquals(1, result.size)
        assertEquals("Jane Doe", result[0].displayName)
    }

    @Test
    fun `phone search matches partial numbers`() {
        val result = filterUseCase(contacts, "123456", ContactSortOrder.NameAscending)
        assertEquals(2, result.size) // Akash (123-456) and Jane (123-4567)
    }

    @Test
    fun `sorting Z-A works correctly`() {
        val result = filterUseCase(contacts, "", ContactSortOrder.NameDescending)
        assertEquals("Zack", result[0].displayName)
        assertEquals("Jane Doe", result[1].displayName)
        assertEquals("Akash Patel", result[2].displayName)
        assertEquals("", result[3].displayName) // Blank name always at the end
    }

    @Test
    fun `no matching results returns empty list`() {
        val result = filterUseCase(contacts, "NonExistent", ContactSortOrder.NameAscending)
        assertTrue(result.isEmpty())
    }
}
