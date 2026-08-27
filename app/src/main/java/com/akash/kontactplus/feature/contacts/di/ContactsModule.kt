package com.akash.kontactplus.feature.contacts.di

import com.akash.kontactplus.feature.contacts.data.datasource.AndroidContactsDataSource
import com.akash.kontactplus.feature.contacts.data.datasource.ContactsDataSource
import com.akash.kontactplus.feature.contacts.data.repository.ContactsRepositoryImpl
import com.akash.kontactplus.feature.contacts.domain.repository.ContactsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ContactsModule {

    @Binds
    @Singleton
    abstract fun bindContactsDataSource(
        androidContactsDataSource: AndroidContactsDataSource
    ): ContactsDataSource

    @Binds
    @Singleton
    abstract fun bindContactsRepository(
        contactsRepositoryImpl: ContactsRepositoryImpl
    ): ContactsRepository
}
