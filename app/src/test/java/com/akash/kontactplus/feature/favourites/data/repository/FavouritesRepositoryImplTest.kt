package com.akash.kontactplus.feature.favourites.data.repository

import com.akash.kontactplus.feature.favourites.data.local.FavouriteContactDao
import com.akash.kontactplus.feature.favourites.data.local.FavouriteContactEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FavouritesRepositoryImplTest {

    private lateinit var repository: FavouritesRepositoryImpl
    private lateinit var fakeDao: FakeFavouriteContactDao

    @Before
    fun setup() {
        fakeDao = FakeFavouriteContactDao()
        repository = FavouritesRepositoryImpl(fakeDao)
    }

    @Test
    fun `addFavourite inserts entity into DAO`() = runBlocking {
        val key = "contact123"
        repository.addFavourite(key)
        
        assertEquals(1, fakeDao.entities.size)
        assertEquals(key, fakeDao.entities[0].lookupKey)
    }

    @Test
    fun `removeFavourite deletes entity from DAO`() = runBlocking {
        val key = "contact123"
        fakeDao.entities.add(FavouriteContactEntity(key, 0L))
        
        repository.removeFavourite(key)
        
        assertEquals(0, fakeDao.entities.size)
    }

    private class FakeFavouriteContactDao : FavouriteContactDao {
        val entities = mutableListOf<FavouriteContactEntity>()

        override fun observeFavouriteLookupKeys(): Flow<List<String>> {
            return flowOf(entities.map { it.lookupKey })
        }

        override fun observeIsFavourite(lookupKey: String): Flow<Boolean> {
            return flowOf(entities.any { it.lookupKey == lookupKey })
        }

        override suspend fun insertFavourite(entity: FavouriteContactEntity) {
            entities.add(entity)
        }

        override suspend fun deleteFavourite(lookupKey: String) {
            entities.removeIf { it.lookupKey == lookupKey }
        }
    }
}
