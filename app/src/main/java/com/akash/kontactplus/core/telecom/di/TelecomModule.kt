package com.akash.kontactplus.core.telecom.di

import com.akash.kontactplus.core.telecom.CallManager
import com.akash.kontactplus.core.telecom.TelecomRoleManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TelecomModule {
    // Both already annotated with @Singleton and @Inject, but we can provide explicitly if needed.
    // For now, Hilt will find them through constructor injection.
}
