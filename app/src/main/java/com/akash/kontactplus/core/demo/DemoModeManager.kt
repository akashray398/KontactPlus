package com.akash.kontactplus.core.demo

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.akash.kontactplus.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "demo_prefs")

@Singleton
class DemoModeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val KEY_DEMO_MODE_ENABLED = booleanPreferencesKey("demo_mode_enabled")

    val isDemoModeEnabled: Flow<Boolean> = if (BuildConfig.DEBUG) {
        context.dataStore.data.map { it[KEY_DEMO_MODE_ENABLED] ?: false }
    } else {
        flowOf(false)
    }

    suspend fun setDemoModeEnabled(enabled: Boolean) {
        if (BuildConfig.DEBUG) {
            context.dataStore.edit { it[KEY_DEMO_MODE_ENABLED] = enabled }
        }
    }
}
