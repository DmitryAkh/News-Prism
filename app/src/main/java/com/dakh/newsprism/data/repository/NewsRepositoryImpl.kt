package com.dakh.newsprism.data.repository

import android.util.Log
import com.dakh.newsprism.data.local.ArticleDbModel
import com.dakh.newsprism.data.local.NewsDao
import com.dakh.newsprism.data.local.SubscriptionDbModel
import com.dakh.newsprism.data.mapper.toArticle
import com.dakh.newsprism.data.mapper.toDbModels
import com.dakh.newsprism.data.remote.NewsApiService
import com.dakh.newsprism.domain.entity.Article
import com.dakh.newsprism.domain.repository.NewsRepository
import jakarta.inject.Inject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

class NewsRepositoryImpl @Inject constructor(
    private val newsDao: NewsDao,
    private val apiService: NewsApiService,
) : NewsRepository {
    override fun getAllSubscriptions(): Flow<List<String>> {
        return newsDao.getAllSubscriptions().map { subscriptions ->
            subscriptions.map {
                it.topic
            }
        }
    }

    override suspend fun addSubscription(topic: String) {
        newsDao.addSubscription(SubscriptionDbModel(topic))
    }

    override suspend fun updateArticlesForTopic(topic: String) {
        val articles = loadArticles(topic)
        newsDao.addArticles(articles)
    }

    private suspend fun loadArticles(topic: String): List<ArticleDbModel> {
        return try {
            apiService.loadArticles(topic).toDbModels(topic)
        } catch (e: Exception) {
            if (e is CancellationException) { // пробрасываем exception наверх в случае отмены корутины
                throw e
            }
            Log.e("NewsRepository", e.stackTraceToString())
            listOf()
        }
    }

    override suspend fun removeSubscription(topic: String) {
        newsDao.deleteSubscription(SubscriptionDbModel(topic))
    }

    override suspend fun updateArticlesForAllSubscriptions() {
        val subscriptions = newsDao.getAllSubscriptions().first()
        coroutineScope {
            subscriptions.forEach {
                launch {
                    updateArticlesForTopic(it.topic)
                }
            }
        }
    }

    override fun getArticlesByTopics(topics: List<String>): Flow<List<Article>> {
        return newsDao.getArticlesByTopics(topics).map {
            it.toArticle()
        }
    }

    override suspend fun clearAllArticles(topics: List<String>) {
        newsDao.deleteArticlesByTopics(topics)
    }
}