package com.dakh.newsprism.domain.usecase

import com.dakh.newsprism.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSubscriptionsUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {

    operator fun invoke(): Flow<List<String>> {
       return newsRepository.getAllSubscriptions()
    }
}