package com.akash.kontactplus.feature.ai.di

import com.akash.kontactplus.feature.ai.data.remote.AiRemoteDataSource
import com.akash.kontactplus.feature.ai.data.remote.RetrofitAiRemoteDataSource
import com.akash.kontactplus.feature.ai.data.repository.AiRepositoryImpl
import com.akash.kontactplus.feature.ai.domain.repository.AiRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {

    @Binds
    @Singleton
    abstract fun bindAiRemoteDataSource(
        impl: RetrofitAiRemoteDataSource
    ): AiRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindAiRepository(
        impl: AiRepositoryImpl
    ): AiRepository
}
