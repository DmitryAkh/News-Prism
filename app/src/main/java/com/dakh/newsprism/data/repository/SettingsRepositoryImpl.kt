package com.dakh.newsprism.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dakh.newsprism.data.mapper.toInterval
import com.dakh.newsprism.domain.entity.Language
import com.dakh.newsprism.domain.entity.Settings
import com.dakh.newsprism.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


class SettingsRepositoryImpl @Inject constructor(
    @param:ApplicationContext val context: Context,
) : SettingsRepository {

    private val languageKey = stringPreferencesKey("language")
    private val intervalKey = intPreferencesKey("interval")
    private val notificationEnabledKey = booleanPreferencesKey("notification_enabled")
    private val wifiOnlyKey = booleanPreferencesKey("wifi_only")

    override fun getSettings(): Flow<Settings> {
        return context.dataStore.data.map { preferences ->

            val languageAsString = preferences[languageKey] ?: Settings.DEFAULT_LANGUAGE.name
            val language = Language.valueOf(languageAsString)

            val interval = preferences[intervalKey]?.toInterval() ?: Settings.DEFAULT_INTERVAL

            val notificationsEnabled = preferences[notificationEnabledKey] ?: Settings.DEFAULT_NOTIFICATIONS_ENABLED
            val wifiOnly = preferences[wifiOnlyKey] ?: Settings.DEFAULT_WIFI_ONLY

            Settings(
                language = language,
                interval = interval,
                notificationsEnabled = notificationsEnabled,
                wifiOnly = wifiOnly
            )
        }
    }

    override suspend fun updateLanguage(language: Language) {
        context.dataStore.updateData {preferences ->
            preferences.toMutablePreferences().also { preferences ->
                preferences[languageKey] = language.name
            }
        }
    }

    override suspend fun updateInterval(minutes: Int) {
        context.dataStore.updateData {preferences ->
            preferences.toMutablePreferences().also { preferences ->
                preferences[intervalKey] = minutes
            }
        }
    }

    override suspend fun updateNotificationsAllow(enabled: Boolean) {
        context.dataStore.updateData {preferences ->
            preferences.toMutablePreferences().also { preferences ->
                preferences[notificationEnabledKey] = enabled
            }
        }
    }

    override suspend fun updateWifiOnly(wifiOnly: Boolean) {
        context.dataStore.updateData {preferences ->
            preferences.toMutablePreferences().also { preferences ->
                preferences[wifiOnlyKey] = wifiOnly
            }
        }
    }
}