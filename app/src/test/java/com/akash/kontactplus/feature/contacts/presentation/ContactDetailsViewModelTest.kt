package com.akash.kontactplus.feature.contacts.presentation

import androidx.lifecycle.SavedStateHandle
import com.akash.kontactplus.feature.contacts.domain.model.Contact
import com.akash.kontactplus.feature.contacts.domain.repository.ContactsRepository
import com.akash.kontactplus.feature.contacts.domain.usecase.GetContactUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ContactDetailsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: ContactDetailsViewModel
    private lateinit var fakeRepository: FakeContactsRepository
    private lateinit var getContactUseCase: GetContactUseCase

    private val lookupKey = "k1"
    private val contact = Contact(1, lookupKey, "Akash")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeContactsRepository()
        getContactUseCase = GetContactUseCase(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `starts in Success if contact exists`() = runTest {
        fakeRepository.contactMap[lookupKey] = contact
        viewModel = ContactDetailsViewModel(
            SavedStateHandle(mapOf(ContactDetailsViewModel.KEY_LOOKUP_KEY to lookupKey)),
            getContactUseCase
        )
        
        assertTrue(viewModel.uiState.value is ContactDetailsUiState.Success)
        assertEquals(contact, (viewModel.uiState.value as ContactDetailsUiState.Success).contact)
    }

    @Test
    fun `starts in NotFound if contact does not exist`() = runTest {
        viewModel = ContactDetailsViewModel(
            SavedStateHandle(mapOf(ContactDetailsViewModel.KEY_LOOKUP_KEY to "unknown")),
            getContactUseCase
        )
        
        assertEquals(ContactDetailsUiState.NotFound, viewModel.uiState.value)
    }

    @Test
    fun `error state when repository fails`() = runTest {
        fakeRepository.shouldReturnError = true
        viewModel = ContactDetailsViewModel(
            SavedStateHandle(mapOf(ContactDetailsViewModel.KEY_LOOKUP_KEY to lookupKey)),
            getContactUseCase
        )
        
        assertTrue(viewModel.uiState.value is ContactDetailsUiState.Error)
    }

    private class FakeContactsRepository : ContactsRepository {
        val contactMap = mutableMapOf<String, Contact>()
        var shouldReturnError = false

        override suspend fun getContacts(): Result<List<Contact>> = Result.success(emptyList())

        override suspend fun getContact(lookupKey: String): Result<Contact?> {
            return if (shouldReturnError) {
                Result.failure(Exception("Error"))
            } else {
                Result.success(contactMap[lookupKey])
            }
        }
    }
}
