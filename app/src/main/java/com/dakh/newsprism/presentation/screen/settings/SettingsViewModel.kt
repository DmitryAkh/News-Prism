package com.dakh.newsprism.presentation.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dakh.newsprism.domain.entity.Interval
import com.dakh.newsprism.domain.entity.Language
import com.dakh.newsprism.domain.entity.Settings
import com.dakh.newsprism.domain.usecase.settings.GetSettingsUseCase
import com.dakh.newsprism.domain.usecase.settings.UpdateIntervalUseCase
import com.dakh.newsprism.domain.usecase.settings.UpdateLanguageUseCase
import com.dakh.newsprism.domain.usecase.settings.UpdateNotificationEnabledUseCase
import com.dakh.newsprism.domain.usecase.settings.UpdateWifiOnlyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateIntervalUseCase: UpdateIntervalUseCase,
    private val updateLanguageUseCase: UpdateLanguageUseCase,
    private val updateNotificationEnabledUseCase: UpdateNotificationEnabledUseCase,
    private val updateWifiOnlyUseCase: UpdateWifiOnlyUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<SettingsState>(SettingsState.Initial)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getSettingsUseCase().collect { settings ->
                _state.update {
                    SettingsState.DisplaySettings(
                        settings = settings
                    )
                }
            }
        }
    }

    fun processCommand(command: SettingsCommand) {
        viewModelScope.launch {
            when (command) {
                is SettingsCommand.SwitchLanguage ->
                    updateLanguageUseCase(command.language)

                is SettingsCommand.SwitchNotificationStatus -> {
                    val current =
                        (state.value as SettingsState.DisplaySettings).settings.notificationsEnabled
                    updateNotificationEnabledUseCase(!current)
                }

                is SettingsCommand.SwitchUpdateInterval ->
                    updateIntervalUseCase(command.interval)

                is SettingsCommand.SwitchWifiOnlyStatus -> {
                    val current =
                        (state.value as SettingsState.DisplaySettings).settings.wifiOnly
                    updateWifiOnlyUseCase(!current)
                }

            }
        }
    }
}

sealed interface SettingsCommand {
    data class SwitchLanguage(val language: Language) : SettingsCommand
    data class SwitchUpdateInterval(val interval: Interval) : SettingsCommand
    data class SwitchNotificationStatus(val notificationsEnabled: Boolean) : SettingsCommand
    data class SwitchWifiOnlyStatus(val wifiOnly: Boolean) : SettingsCommand
}

sealed interface SettingsState {
    data object Initial : SettingsState
    data class DisplaySettings(
        val settings: Settings,
        val languages: List<Language> = Language.entries,
        val intervals: List<Interval> = Interval.entries,
    ) : SettingsState
}
