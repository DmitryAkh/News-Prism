package com.dakh.newsprism.domain.usecase.settings

import com.dakh.newsprism.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateWifiOnlyUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {

     suspend operator fun invoke(wifiOnly: Boolean) = settingsRepository.updateWifiOnly(wifiOnly)

}