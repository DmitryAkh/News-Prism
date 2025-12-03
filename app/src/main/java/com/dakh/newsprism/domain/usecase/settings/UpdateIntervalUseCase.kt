package com.dakh.newsprism.domain.usecase.settings

import com.dakh.newsprism.domain.entity.Interval
import com.dakh.newsprism.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateIntervalUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {

     suspend operator fun invoke(interval: Interval) = settingsRepository.updateInterval(interval.minutes)

}