package com.dakh.newsprism.domain.usecase.settings

import com.dakh.newsprism.domain.entity.Interval
import com.dakh.newsprism.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateIntervalUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {

     suspend operator fun invoke(interval: Interval) = repository.updateInterval(interval.minutes)

}