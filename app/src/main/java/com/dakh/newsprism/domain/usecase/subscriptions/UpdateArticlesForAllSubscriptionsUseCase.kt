package com.dakh.newsprism.domain.usecase.subscriptions

import com.dakh.newsprism.domain.repository.NewsRepository
import com.dakh.newsprism.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateArticlesForAllSubscriptionsUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(): List<String> {
        val language = settingsRepository.getSettings().first().language
        return newsRepository.updateArticlesForAllSubscriptions(language)
    }
}