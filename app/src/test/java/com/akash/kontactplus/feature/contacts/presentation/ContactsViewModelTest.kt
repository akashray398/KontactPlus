package com.akash.kontactplus.feature.contacts.presentation

import androidx.lifecycle.SavedStateHandle
import com.akash.kontactplus.feature.contacts.domain.model.Contact
import com.akash.kontactplus.feature.contacts.domain.repository.ContactsRepository
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

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeContactsRepository()
        getContactsUseCase = GetContactsUseCase(fakeRepository)
        viewModel = ContactsViewModel(SavedStateHandle(), getContactsUseCase)
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
    fun `onPermissionStatusChecked with granted=true triggers contact loading`() = runTest {
        val contacts = listOf(Contact(1, "k1", "Akash"))
        fakeRepository.contacts = contacts
        
        viewModel.onPermissionStatusChecked(isGranted = true, shouldShowRationale = false)
        
        assertEquals(ContactsPermissionState.Granted, viewModel.uiState.value.permissionState)
        assertEquals(contacts, viewModel.uiState.value.contacts)
        assertTrue(viewModel.uiState.value.hasLoadedContacts)
    }

    @Test
    fun `onPermissionStatusChecked with granted=false and never requested sets NotRequested`() {
        viewModel.onPermissionStatusChecked(isGranted = false, shouldShowRationale = false)
        assertEquals(ContactsPermissionState.NotRequested, viewModel.uiState.value.permissionState)
        assertTrue(viewModel.uiState.value.contacts.isEmpty())
    }

    @Test
    fun `onPermissionResultReceived with granted=true loads contacts`() = runTest {
        val contacts = listOf(Contact(1, "k1", "Akash"))
        fakeRepository.contacts = contacts
        
        viewModel.onPermissionResultReceived(isGranted = true, shouldShowRationale = false)
        
        assertEquals(contacts, viewModel.uiState.value.contacts)
    }

    @Test
    fun `permission revocation clears contacts`() = runTest {
        fakeRepository.contacts = listOf(Contact(1, "k1", "Akash"))
        viewModel.onPermissionResultReceived(isGranted = true, shouldShowRationale = false)
        
        assertFalse(viewModel.uiState.value.contacts.isEmpty())
        
        viewModel.onPermissionStatusChecked(isGranted = false, shouldShowRationale = true)
        
        assertTrue(viewModel.uiState.value.contacts.isEmpty())
        assertFalse(viewModel.uiState.value.hasLoadedContacts)
    }

    @Test
    fun `failed contact loading updates error state`() = runTest {
        fakeRepository.shouldReturnError = true
        
        viewModel.onPermissionResultReceived(isGranted = true, shouldShowRationale = false)
        
        assertTrue(viewModel.uiState.value.contacts.isEmpty())
        assertEquals(com.akash.kontactplus.R.string.contacts_load_error_description, viewModel.uiState.value.errorMessageRes)
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
    }
}
