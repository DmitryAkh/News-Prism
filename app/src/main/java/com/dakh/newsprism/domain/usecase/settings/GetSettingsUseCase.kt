package com.dakh.newsprism.domain.usecase.settings

import com.dakh.newsprism.domain.repository.SettingsRepository
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {

    operator fun invoke() = repository.getSettings()

}