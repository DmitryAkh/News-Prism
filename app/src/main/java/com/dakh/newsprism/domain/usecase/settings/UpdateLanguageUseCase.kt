package com.dakh.newsprism.domain.usecase.settings

import com.dakh.newsprism.domain.entity.Language
import com.dakh.newsprism.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateLanguageUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {

     suspend operator fun invoke(language: Language) = repository.updateLanguage(language)

}