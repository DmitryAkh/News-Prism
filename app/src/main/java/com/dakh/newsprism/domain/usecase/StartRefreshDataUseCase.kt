package com.dakh.newsprism.domain.usecase

import com.dakh.newsprism.data.mapper.toRefreshConfig
import com.dakh.newsprism.domain.repository.NewsRepository
import com.dakh.newsprism.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class StartRefreshDataUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke() {
       val settings = settingsRepository.getSettings()
        settingsRepository.getSettings()
            .map { it.toRefreshConfig() }
            .distinctUntilChanged()
            .onEach { newsRepository.startBackgroundRefresh(it) }
            .collect()
    }
}