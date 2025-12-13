package com.dakh.newsprism.domain.usecase.settings

import com.dakh.newsprism.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateNotificationEnabledUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {

     suspend operator fun invoke(enabled: Boolean) = settingsRepository.updateNotificationsAllow(enabled)

}