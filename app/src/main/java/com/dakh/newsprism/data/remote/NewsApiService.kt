package com.dakh.newsprism.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("v2/everything?apiKey=af46a7b460bc4ec1833c0c9d387f441e")
    suspend fun loadArticles(
        @Query("q") topic: String,
    ): NewsResponseDto
}