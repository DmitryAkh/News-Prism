package com.dakh.newsprism.domain.usecase.subscriptions

import com.dakh.newsprism.domain.repository.NewsRepository
import javax.inject.Inject

class UpdateArticlesForAllSubscriptionsUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke() {
        newsRepository.updateArticlesForAllSubscriptions()
    }
}