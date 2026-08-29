package com.akash.kontactplus.feature.recents.di

import com.akash.kontactplus.feature.recents.data.datasource.AndroidCallLogDataSource
import com.akash.kontactplus.feature.recents.data.datasource.CallLogDataSource
import com.akash.kontactplus.feature.recents.data.repository.CallLogRepositoryImpl
import com.akash.kontactplus.feature.recents.domain.repository.CallLogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RecentsModule {

    @Binds
    @Singleton
    abstract fun bindCallLogDataSource(
        impl: AndroidCallLogDataSource
    ): CallLogDataSource

    @Binds
    @Singleton
    abstract fun bindCallLogRepository(
        impl: CallLogRepositoryImpl
    ): CallLogRepository
}
