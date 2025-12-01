package com.dakh.newsprism.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dakh.newsprism.domain.repository.NewsRepository
import com.dakh.newsprism.presentation.screens.subscriptions.SubscriptionsScreen
import com.dakh.newsprism.ui.theme.NewsPrismTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: NewsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NewsPrismTheme {
                SubscriptionsScreen(
                    onNavigateToSettings = {

                    }
                )
            }
        }
    }
}
