package com.akash.kontactplus.feature.favourites.presentation

import com.akash.kontactplus.feature.contacts.domain.model.Contact
import com.akash.kontactplus.feature.contacts.domain.repository.ContactsRepository
import com.akash.kontactplus.feature.favourites.domain.repository.FavouritesRepository
import com.akash.kontactplus.feature.favourites.domain.usecase.ObserveFavouriteContactsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
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
class FavouritesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: FavouritesViewModel
    private lateinit var fakeFavouritesRepository: FakeFavouritesRepository
    private lateinit var fakeContactsRepository: FakeContactsRepository
    private lateinit var useCase: ObserveFavouriteContactsUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeFavouritesRepository = FakeFavouritesRepository()
        fakeContactsRepository = FakeContactsRepository()
        useCase = ObserveFavouriteContactsUseCase(fakeFavouritesRepository, fakeContactsRepository)
        viewModel = FavouritesViewModel(useCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state shows empty list`() = runTest {
        assertTrue(viewModel.uiState.value.contacts.isEmpty())
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `onPermissionStatusChanged false clears contacts`() = runTest {
        val contact = Contact(1, "k1", "Akash")
        fakeContactsRepository.contacts = listOf(contact)
        fakeFavouritesRepository.favouriteKeys = listOf("k1")
        
        // Trigger observation
        viewModel.onPermissionStatusChanged(true)
        assertEquals(1, viewModel.uiState.value.contacts.size)
        assertEquals("Akash", viewModel.uiState.value.contacts[0].displayName)

        viewModel.onPermissionStatusChanged(false)
        assertTrue(viewModel.uiState.value.contacts.isEmpty())
        assertFalse(viewModel.uiState.value.hasContactsPermission)
    }

    private class FakeFavouritesRepository : FavouritesRepository {
        var favouriteKeys = emptyList<String>()
        override fun observeFavouriteLookupKeys(): Flow<List<String>> = flowOf(favouriteKeys)
        override fun observeIsFavourite(lookupKey: String): Flow<Boolean> = flowOf(favouriteKeys.contains(lookupKey))
        override suspend fun addFavourite(lookupKey: String) {}
        override suspend fun removeFavourite(lookupKey: String) {}
    }

    private class FakeContactsRepository : ContactsRepository {
        var contacts = emptyList<Contact>()
        override suspend fun getContacts(): Result<List<Contact>> = Result.success(contacts)
        override suspend fun getContact(lookupKey: String): Result<Contact?> = Result.success(contacts.find { it.lookupKey == lookupKey })
    }
}
