package com.akash.kontactplus.feature.contacts.presentation

import androidx.lifecycle.SavedStateHandle
import com.akash.kontactplus.feature.contacts.domain.model.Contact
import com.akash.kontactplus.feature.contacts.domain.model.ContactSortOrder
import com.akash.kontactplus.feature.contacts.domain.repository.ContactsRepository
import com.akash.kontactplus.feature.contacts.domain.usecase.FilterContactsUseCase
import com.akash.kontactplus.feature.contacts.domain.usecase.GetContactsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ContactsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: ContactsViewModel
    private lateinit var fakeRepository: FakeContactsRepository
    private lateinit var getContactsUseCase: GetContactsUseCase
    private lateinit var filterContactsUseCase: FilterContactsUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeContactsRepository()
        getContactsUseCase = GetContactsUseCase(fakeRepository)
        filterContactsUseCase = FilterContactsUseCase()
        viewModel = ContactsViewModel(SavedStateHandle(), getContactsUseCase, filterContactsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Checking`() {
        assertEquals(ContactsPermissionState.Checking, viewModel.uiState.value.permissionState)
    }

    @Test
    fun `onPermissionStatusChecked with granted=true triggers contact loading and filtering`() = runTest {
        val contacts = listOf(Contact(1, "k1", "Akash"))
        fakeRepository.contacts = contacts
        
        viewModel.onPermissionStatusChecked(isGranted = true, shouldShowRationale = false)
        
        assertEquals(ContactsPermissionState.Granted, viewModel.uiState.value.permissionState)
        assertEquals(contacts, viewModel.uiState.value.visibleContacts)
        assertTrue(viewModel.uiState.value.hasLoadedContacts)
    }

    @Test
    fun `search query filters visible contacts`() = runTest {
        fakeRepository.contacts = listOf(
            Contact(1, "k1", "Akash"),
            Contact(2, "k2", "Jane")
        )
        viewModel.onPermissionStatusChecked(isGranted = true, shouldShowRationale = false)
        
        viewModel.onSearchQueryChanged("Akash")
        
        assertEquals(1, viewModel.uiState.value.visibleContacts.size)
        assertEquals("Akash", viewModel.uiState.value.visibleContacts[0].displayName)
    }

    @Test
    fun `clearing search restores visible contacts`() = runTest {
        fakeRepository.contacts = listOf(
            Contact(1, "k1", "Akash"),
            Contact(2, "k2", "Jane")
        )
        viewModel.onPermissionStatusChecked(isGranted = true, shouldShowRationale = false)
        viewModel.onSearchQueryChanged("Akash")
        
        viewModel.onClearSearch()
        
        assertEquals(2, viewModel.uiState.value.visibleContacts.size)
    }

    @Test
    fun `sort order change updates visible contacts`() = runTest {
        fakeRepository.contacts = listOf(
            Contact(1, "k1", "Akash"),
            Contact(2, "k2", "Jane")
        )
        viewModel.onPermissionStatusChecked(isGranted = true, shouldShowRationale = false)
        
        viewModel.onSortOrderChanged(ContactSortOrder.NameDescending)
        
        assertEquals("Jane", viewModel.uiState.value.visibleContacts[0].displayName)
    }

    @Test
    fun `permission revocation clears contacts and visible contacts`() = runTest {
        fakeRepository.contacts = listOf(Contact(1, "k1", "Akash"))
        viewModel.onPermissionResultReceived(isGranted = true, shouldShowRationale = false)
        
        assertFalse(viewModel.uiState.value.visibleContacts.isEmpty())
        
        viewModel.onPermissionStatusChecked(isGranted = false, shouldShowRationale = true)
        
        assertTrue(viewModel.uiState.value.visibleContacts.isEmpty())
        assertFalse(viewModel.uiState.value.hasLoadedContacts)
    }

    private class FakeContactsRepository : ContactsRepository {
        var contacts = emptyList<Contact>()
        var shouldReturnError = false

        override suspend fun getContacts(): Result<List<Contact>> {
            return if (shouldReturnError) {
                Result.failure(Exception("Provider error"))
            } else {
                Result.success(contacts)
            }
        }

        override suspend fun getContact(lookupKey: String): Result<Contact?> {
            return Result.success(contacts.find { it.lookupKey == lookupKey })
        }
    }
}
