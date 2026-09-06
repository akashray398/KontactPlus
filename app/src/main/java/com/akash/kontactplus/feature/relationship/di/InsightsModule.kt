package com.akash.kontactplus.feature.relationship.di

import com.akash.kontactplus.feature.relationship.data.repository.ConnectionInsightsRepositoryImpl
import com.akash.kontactplus.feature.relationship.data.repository.InteractionInsightsRepositoryImpl
import com.akash.kontactplus.feature.relationship.domain.repository.ConnectionInsightsRepository
import com.akash.kontactplus.feature.relationship.domain.repository.InteractionInsightsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InsightsModule {

    @Binds
    @Singleton
    abstract fun bindConnectionInsightsRepository(
        impl: ConnectionInsightsRepositoryImpl
    ): ConnectionInsightsRepository

    @Binds
    @Singleton
    abstract fun bindInteractionInsightsRepository(
        impl: InteractionInsightsRepositoryImpl
    ): InteractionInsightsRepository
}
