package com.dakh.newsprism.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.dakh.newsprism.domain.repository.NewsRepository
import com.dakh.newsprism.presentation.theme.NewsPrismTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: NewsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            repository.addSubscription("Kotlin")
            repository.updateArticlesForTopic("Kotlin")
//            Log.d("MainActivity", repository.getArticlesByTopics())
        }
        setContent {
            NewsPrismTheme {

            }
        }
    }
}
