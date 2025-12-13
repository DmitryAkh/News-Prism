package com.dakh.newsprism.presentation.screen.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakh.newsprism.domain.entity.Article
import com.dakh.newsprism.domain.usecase.subscriptions.AddSubscriptionUseCase
import com.dakh.newsprism.domain.usecase.subscriptions.ClearAllArticlesUseCase
import com.dakh.newsprism.domain.usecase.subscriptions.GetAllSubscriptionsUseCase
import com.dakh.newsprism.domain.usecase.subscriptions.GetArticlesByTopicsUseCase
import com.dakh.newsprism.domain.usecase.subscriptions.RemoveSubscriptionUseCase
import com.dakh.newsprism.domain.usecase.subscriptions.UpdateArticlesForAllSubscriptionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val clearAllArticlesUseCase: ClearAllArticlesUseCase,
    private val addSubscriptionUseCase: AddSubscriptionUseCase,
    private val getArticlesByTopicsUseCase: GetArticlesByTopicsUseCase,
    private val getAllSubscriptionsUseCase: GetAllSubscriptionsUseCase,
    private val removeSubscriptionUseCase: RemoveSubscriptionUseCase,
    private val updateArticlesForAllSubscriptionsUseCase: UpdateArticlesForAllSubscriptionsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SubscriptionState())
    val state = _state.asStateFlow()

    init {
        observeSubscriptions()
        observeSelectedTopics()
    }

    fun processCommand(command: SubscriptionsCommand) {
        when (command) {
            SubscriptionsCommand.ClearArticles -> {
                viewModelScope.launch {
                    val selectedTopics = _state.value.selectedTopics
                    clearAllArticlesUseCase(selectedTopics)
                }
            }

            SubscriptionsCommand.ClickSubscribe -> {
                viewModelScope.launch {
                    _state.update { prevState ->
                        val topic = _state.value.query.trim()
                        addSubscriptionUseCase(topic)
                        prevState.copy(query = "")
                    }

                }
            }

            is SubscriptionsCommand.InputTopic -> {
                _state.update { prevState ->
                    prevState.copy(query = command.query)
                }
            }

            SubscriptionsCommand.RefreshData -> {
                viewModelScope.launch {
                    updateArticlesForAllSubscriptionsUseCase()
                }
            }

            is SubscriptionsCommand.RemoveSubscription -> {
                viewModelScope.launch {
                    removeSubscriptionUseCase(command.topic)
                }
            }

            is SubscriptionsCommand.ToggleTopicSelection -> {
                _state.update { prevState ->
                    val subscriptions = prevState.subscriptions.toMutableMap()
                    val isSelected = subscriptions[command.topic] ?: false
                    subscriptions[command.topic] = !isSelected
                    prevState.copy(subscriptions = subscriptions)
                }
            }
        }
    }

    private fun observeSelectedTopics() {
        state.map { it.selectedTopics }
            .distinctUntilChanged()
            .flatMapLatest {
                getArticlesByTopicsUseCase(it)
            }
            .onEach { newArticles ->
                _state.update { prevState ->
                    prevState.copy(articles = newArticles)
                }
            }.launchIn(viewModelScope)
    }

    private fun observeSubscriptions() {
        getAllSubscriptionsUseCase()
            .onEach { subscriptions ->
                _state.update { prevState ->
                    val updatedTopics = subscriptions.associateWith { topic ->
                        prevState.subscriptions[topic] ?: true
                    }
                    prevState.copy(subscriptions = updatedTopics)
                }
            }.launchIn(viewModelScope)
    }
}

sealed interface SubscriptionsCommand {

    data class InputTopic(val query: String) : SubscriptionsCommand

    data object ClickSubscribe : SubscriptionsCommand

    data object RefreshData : SubscriptionsCommand

    data class ToggleTopicSelection(val topic: String) : SubscriptionsCommand

    data object ClearArticles : SubscriptionsCommand

    data class RemoveSubscription(val topic: String) : SubscriptionsCommand

}

data class SubscriptionState(
    val query: String = "",
    val subscriptions: Map<String, Boolean> = mapOf(),
    val articles: List<Article> = listOf(),
) {
    val subscribeButtonEnabled: Boolean
        get() = query.isNotBlank()

    val selectedTopics: List<String>
        get() = subscriptions.filter {
            it.value
        }.map { it.key }
}