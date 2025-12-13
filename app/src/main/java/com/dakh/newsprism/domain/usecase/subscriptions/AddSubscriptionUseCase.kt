package com.dakh.newsprism.domain.usecase.subscriptions

import com.dakh.newsprism.domain.repository.NewsRepository
import com.dakh.newsprism.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddSubscriptionUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(topic: String) {
        newsRepository.addSubscription(topic)
        val language = settingsRepository.getSettings().first().language
        CoroutineScope(currentCoroutineContext()).launch {
            newsRepository.updateArticlesForTopic(topic, language)
        }
    }

}