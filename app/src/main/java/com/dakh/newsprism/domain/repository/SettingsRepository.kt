package com.dakh.newsprism.domain.repository

import com.dakh.newsprism.domain.entity.Language
import com.dakh.newsprism.domain.entity.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    fun getSettings(): Flow<Settings>

    suspend fun updateLanguage(language: Language)

    suspend fun updateInterval(minutes: Int)

    suspend fun updateNotificationsAllow(enabled: Boolean)

    suspend fun updateWifiOnly(wifiOnly: Boolean)
}