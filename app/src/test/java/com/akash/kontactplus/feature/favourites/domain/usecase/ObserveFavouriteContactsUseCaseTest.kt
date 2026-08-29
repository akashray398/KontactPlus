package com.akash.kontactplus.feature.favourites.domain.usecase

import com.akash.kontactplus.feature.contacts.domain.model.Contact
import com.akash.kontactplus.feature.contacts.domain.repository.ContactsRepository
import com.akash.kontactplus.feature.favourites.domain.repository.FavouritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ObserveFavouriteContactsUseCaseTest {

    private lateinit var useCase: ObserveFavouriteContactsUseCase
    private lateinit var fakeFavouritesRepository: FakeFavouritesRepository
    private lateinit var fakeContactsRepository: FakeContactsRepository

    @Before
    fun setup() {
        fakeFavouritesRepository = FakeFavouritesRepository()
        fakeContactsRepository = FakeContactsRepository()
        useCase = ObserveFavouriteContactsUseCase(fakeFavouritesRepository, fakeContactsRepository)
    }

    @Test
    fun `resolves favourite keys to contact objects`() = runBlocking {
        val contacts = listOf(
            Contact(1, "k1", "Akash"),
            Contact(2, "k2", "Jane")
        )
        fakeContactsRepository.contacts = contacts
        fakeFavouritesRepository.favouriteKeys = listOf("k1")

        val result = useCase().first()

        assertEquals(1, result.size)
        assertEquals("Akash", result[0].displayName)
    }

    @Test
    fun `skips deleted contacts`() = runBlocking {
        fakeContactsRepository.contacts = listOf(Contact(1, "k1", "Akash"))
        fakeFavouritesRepository.favouriteKeys = listOf("k1", "deleted_key")

        val result = useCase().first()

        assertEquals(1, result.size)
        assertEquals("Akash", result[0].displayName)
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
