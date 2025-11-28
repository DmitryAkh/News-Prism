package com.dakh.newsprism.data.mapper

import com.dakh.newsprism.data.local.ArticleDbModel
import com.dakh.newsprism.data.remote.NewsResponseDto
import com.dakh.newsprism.domain.entity.Article
import java.text.SimpleDateFormat
import java.util.Locale

fun NewsResponseDto.toDbModels(topic: String): List<ArticleDbModel> {
    return articles.map {
        ArticleDbModel(
            title = it.title,
            description = it.description,
            imageUrl = it.urlToImage,
            sourceName = it.source.name,
            publishedAt = it.publishedAt.toTimeStamp(),
            url = it.url,
            topic = topic
        )
    }
}

    fun List<ArticleDbModel>.toArticle(): List<Article> {
        return map {
            Article(
                title = it.title,
                description = it.description,
                imageUrl = it.imageUrl,
                sourceName = it.sourceName,
                publishedAt = it.publishedAt,
                url = it.url
            )
        }.distinct()
    }

private fun String.toTimeStamp(): Long {
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    return dateFormatter.parse(this)?.time ?: System.currentTimeMillis()
}