package com.dakh.newsprism.domain.usecase.settings

import com.dakh.newsprism.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateWifiOnlyUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {

     suspend operator fun invoke(wifiOnly: Boolean) = repository.updateWifiOnly(wifiOnly)

}