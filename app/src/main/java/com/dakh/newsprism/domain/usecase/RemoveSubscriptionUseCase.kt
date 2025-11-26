package com.dakh.newsprism.domain.usecase

import com.dakh.newsprism.domain.repository.NewsRepository
import javax.inject.Inject

class RemoveSubscriptionUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(topic: String) {
        newsRepository.removeSubscription(topic)
    }
}