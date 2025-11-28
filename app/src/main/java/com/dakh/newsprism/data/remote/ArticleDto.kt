package com.dakh.newsprism.data.remote

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ArticleDto(
    @SerialName("source")
    val source: SourceDto = SourceDto(),
    @SerialName("title")
    val title: String = "",
    @SerialName("description")
    val description: String = "",
    @SerialName("url")
    val url: String = "",
    @SerialName("urlToImage")
    val urlToImage: String? = "",
    @SerialName("publishedAt")
    val publishedAt: String = "",
    )