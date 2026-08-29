package com.akash.kontactplus.core.database.di

import android.content.Context
import androidx.room.Room
import com.akash.kontactplus.core.database.KontactPlusDatabase
import com.akash.kontactplus.feature.favourites.data.local.FavouriteContactDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): KontactPlusDatabase {
        return Room.databaseBuilder(
            context,
            KontactPlusDatabase::class.java,
            "kontactplus.db"
        ).build()
    }

    @Provides
    fun provideFavouriteContactDao(
        database: KontactPlusDatabase
    ): FavouriteContactDao {
        return database.favouriteContactDao()
    }
}
