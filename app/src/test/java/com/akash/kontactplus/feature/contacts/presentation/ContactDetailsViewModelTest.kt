package com.akash.kontactplus.feature.contacts.presentation

import androidx.lifecycle.SavedStateHandle
import com.akash.kontactplus.feature.contacts.domain.model.Contact
import com.akash.kontactplus.feature.contacts.domain.repository.ContactsRepository
import com.akash.kontactplus.feature.contacts.domain.usecase.GetContactUseCase
import com.akash.kontactplus.feature.favourites.domain.repository.FavouritesRepository
import com.akash.kontactplus.feature.favourites.domain.usecase.IsContactFavouriteUseCase
import com.akash.kontactplus.feature.favourites.domain.usecase.ToggleFavouriteContactUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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
    private lateinit var fakeContactsRepository: FakeContactsRepository
    private lateinit var fakeFavouritesRepository: FakeFavouritesRepository
    private lateinit var getContactUseCase: GetContactUseCase
    private lateinit var isContactFavouriteUseCase: IsContactFavouriteUseCase
    private lateinit var toggleFavouriteContactUseCase: ToggleFavouriteContactUseCase

    private val lookupKey = "k1"
    private val contact = Contact(1, lookupKey, "Akash")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeContactsRepository = FakeContactsRepository()
        fakeFavouritesRepository = FakeFavouritesRepository()
        getContactUseCase = GetContactUseCase(fakeContactsRepository)
        isContactFavouriteUseCase = IsContactFavouriteUseCase(fakeFavouritesRepository)
        toggleFavouriteContactUseCase = ToggleFavouriteContactUseCase(fakeFavouritesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `starts in Success if contact exists`() = runTest {
        fakeContactsRepository.contactMap[lookupKey] = contact
        createViewModel()
        
        assertTrue(viewModel.uiState.value is ContactDetailsUiState.Success)
        assertEquals(contact, (viewModel.uiState.value as ContactDetailsUiState.Success).contact)
    }

    @Test
    fun `starts in NotFound if contact does not exist`() = runTest {
        createViewModel("unknown")
        
        assertEquals(ContactDetailsUiState.NotFound, viewModel.uiState.value)
    }

    @Test
    fun `error state when repository fails`() = runTest {
        fakeContactsRepository.shouldReturnError = true
        createViewModel()
        
        assertTrue(viewModel.uiState.value is ContactDetailsUiState.Error)
    }

    @Test
    fun `isFavourite state updates correctly`() = runTest {
        fakeContactsRepository.contactMap[lookupKey] = contact
        fakeFavouritesRepository.favouriteKeys = listOf(lookupKey)
        createViewModel()
        
        val state = viewModel.uiState.value as ContactDetailsUiState.Success
        assertTrue(state.isFavourite)
    }

    private fun createViewModel(key: String = lookupKey) {
        viewModel = ContactDetailsViewModel(
            SavedStateHandle(mapOf(ContactDetailsViewModel.KEY_LOOKUP_KEY to key)),
            getContactUseCase,
            isContactFavouriteUseCase,
            toggleFavouriteContactUseCase
        )
    }

    private class FakeContactsRepository : ContactsRepository {
        val contactMap = mutableMapOf<String, Contact>()
        var shouldReturnError = false

        override suspend fun getContacts(): Result<List<Contact>> = Result.success(emptyOf())

        override suspend fun getContact(lookupKey: String): Result<Contact?> {
            return if (shouldReturnError) {
                Result.failure(Exception("Error"))
            } else {
                Result.success(contactMap[lookupKey])
            }
        }
        
        private fun <T> emptyOf(): List<T> = emptyList()
    }

    private class FakeFavouritesRepository : FavouritesRepository {
        var favouriteKeys = emptyList<String>()
        override fun observeFavouriteLookupKeys(): Flow<List<String>> = flowOf(favouriteKeys)
        override fun observeIsFavourite(lookupKey: String): Flow<Boolean> = flowOf(favouriteKeys.contains(lookupKey))
        override suspend fun addFavourite(lookupKey: String) {}
        override suspend fun removeFavourite(lookupKey: String) {}
    }
}
