package com.dakh.newsprism.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.dakh.newsprism.data.local.NewsDao
import com.dakh.newsprism.data.local.NewsDataBase
import com.dakh.newsprism.data.remote.NewsApiService
import com.dakh.newsprism.data.repository.NewsRepositoryImpl
import com.dakh.newsprism.domain.repository.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    @Singleton
    fun bindNewsRepository(
        impl: NewsRepositoryImpl,
    ): NewsRepository


    companion object {

        @Provides
        @Singleton
        fun provideWorkManager(
            @ApplicationContext context: Context,
        ): WorkManager {
            return WorkManager.getInstance(context)
        }

        @Provides
        @Singleton
        fun provideJson(): Json {
            return Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
        }

        @Provides
        @Singleton
        fun provideConverterFactory(
            json: Json,
        ): Converter.Factory {
            return json.asConverterFactory(
                "application/json".toMediaType()
            )
        }

        @Provides
        @Singleton
        fun provideRetrofit(
            converter: Converter.Factory,
        ): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://newsapi.org/")
                .addConverterFactory(converter)
                .build()

        }

        @Provides
        @Singleton
        fun provideApiService(
            retrofit: Retrofit,
        ): NewsApiService {
            return retrofit.create()
        }

        @Singleton
        @Provides
        fun provideDatabase(
            @ApplicationContext context: Context,
        ): NewsDataBase {
            return Room.databaseBuilder(
                context = context,
                klass = NewsDataBase::class.java,
                name = "news.db"
            ).fallbackToDestructiveMigration(dropAllTables = true).build()
        }

        @Singleton
        @Provides
        fun provideNewsDao(dataBase: NewsDataBase): NewsDao {
            return dataBase.newsDao()
        }
    }
}