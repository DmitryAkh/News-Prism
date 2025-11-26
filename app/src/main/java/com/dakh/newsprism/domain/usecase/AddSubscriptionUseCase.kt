package com.dakh.newsprism.domain.usecase

import com.dakh.newsprism.domain.repository.NewsRepository
import javax.inject.Inject

class AddSubscriptionUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
) {

    suspend operator fun invoke(topic: String) {
        newsRepository.addSubscription(topic)
        newsRepository.updateArticlesForTopic(topic)
    }

}