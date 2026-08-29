package com.akash.kontactplus.feature.favourites.di

import com.akash.kontactplus.feature.favourites.data.repository.FavouritesRepositoryImpl
import com.akash.kontactplus.feature.favourites.domain.repository.FavouritesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FavouritesModule {

    @Binds
    @Singleton
    abstract fun bindFavouritesRepository(
        favouritesRepositoryImpl: FavouritesRepositoryImpl
    ): FavouritesRepository
}
