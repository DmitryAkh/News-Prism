package com.dakh.newsprism.data.mapper

import com.dakh.newsprism.domain.entity.RefreshConfig
import com.dakh.newsprism.domain.entity.Settings

fun Settings.toRefreshConfig(): RefreshConfig {
    return RefreshConfig(
        language = language,
        interval = interval,
        wifiOnly = wifiOnly
    )
}