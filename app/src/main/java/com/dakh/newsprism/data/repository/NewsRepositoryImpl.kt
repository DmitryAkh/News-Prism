package com.dakh.newsprism.data.repository

import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dakh.newsprism.data.background.RefreshDataWorker
import com.dakh.newsprism.data.local.ArticleDbModel
import com.dakh.newsprism.data.local.NewsDao
import com.dakh.newsprism.data.local.SubscriptionDbModel
import com.dakh.newsprism.data.mapper.toArticle
import com.dakh.newsprism.data.mapper.toDbModels
import com.dakh.newsprism.data.mapper.toQueryParam
import com.dakh.newsprism.data.remote.NewsApiService
import com.dakh.newsprism.domain.entity.Article
import com.dakh.newsprism.domain.entity.Language
import com.dakh.newsprism.domain.entity.RefreshConfig
import com.dakh.newsprism.domain.repository.NewsRepository
import jakarta.inject.Inject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit

class NewsRepositoryImpl @Inject constructor(
    private val newsDao: NewsDao,
    private val apiService: NewsApiService,
    private val workManager: WorkManager,
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

    override suspend fun updateArticlesForTopic(topic: String, language: Language): Boolean {
        val articles = loadArticles(topic, language)
        val ids = newsDao.addArticles(articles)
        return !ids.contains((-1).toLong())
    }

    private suspend fun loadArticles(topic: String, language: Language): List<ArticleDbModel> {
        return try {
            apiService.loadArticles(topic, language.toQueryParam()).toDbModels(topic)
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

    override suspend fun updateArticlesForAllSubscriptions(language: Language): List<String> {
        val updatedTopics = mutableListOf<String>()
        val subscriptions = newsDao.getAllSubscriptions().first()
        coroutineScope {
            subscriptions.forEach {
                launch {
                    val updated = updateArticlesForTopic(it.topic, language)
                    if (updated) {
                        updatedTopics.add(it.topic)
                    }
                }
            }
        }
        return updatedTopics
    }

    override fun getArticlesByTopics(topics: List<String>): Flow<List<Article>> {
        return newsDao.getArticlesByTopics(topics).map {
            it.toArticle()
        }
    }

    override fun startBackgroundRefresh(refreshConfig: RefreshConfig) {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                if (refreshConfig.wifiOnly) {
                    NetworkType.UNMETERED
                } else {
                    NetworkType.CONNECTED
                }
            )
            .setRequiresBatteryNotLow(true)
            .build()


        val request = PeriodicWorkRequestBuilder<RefreshDataWorker>(
            refreshConfig.interval.minutes.toLong(),
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()
        workManager.enqueueUniquePeriodicWork(
            uniqueWorkName = "Refresh data",
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request = request
        )
    }

    override suspend fun clearAllArticles(topics: List<String>) {
        newsDao.deleteArticlesByTopics(topics)
    }
}