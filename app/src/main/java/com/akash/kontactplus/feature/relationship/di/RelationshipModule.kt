package com.akash.kontactplus.feature.relationship.di

import com.akash.kontactplus.feature.relationship.data.repository.RelationshipRepositoryImpl
import com.akash.kontactplus.feature.relationship.data.scheduler.AndroidRelationshipReminderScheduler
import com.akash.kontactplus.feature.relationship.domain.repository.RelationshipRepository
import com.akash.kontactplus.feature.relationship.domain.scheduler.RelationshipReminderScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RelationshipModule {

    @Binds
    @Singleton
    abstract fun bindRelationshipRepository(
        impl: RelationshipRepositoryImpl
    ): RelationshipRepository

    @Binds
    @Singleton
    abstract fun bindRelationshipReminderScheduler(
        impl: AndroidRelationshipReminderScheduler
    ): RelationshipReminderScheduler
}
