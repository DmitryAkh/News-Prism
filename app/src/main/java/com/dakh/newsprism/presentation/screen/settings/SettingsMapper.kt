package com.dakh.newsprism.presentation.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dakh.newsprism.R
import com.dakh.newsprism.domain.entity.Interval
import com.dakh.newsprism.domain.entity.Language

@Composable
fun Language.asString(): String {
    return when (this) {
        Language.ENGLISH -> stringResource(R.string.english)
        Language.RUSSIAN -> stringResource(R.string.russian)
        Language.FRENCH -> stringResource(R.string.francais)
        Language.GERMAN -> stringResource(R.string.german)
    }
}

@Composable
fun Interval.asString(): String {
    return when (this) {
        Interval.MIN_15 -> stringResource(R.string._15_minutes)
        Interval.MIN_30 -> stringResource(R.string._30_minutes)
        Interval.HOUR_1 -> stringResource(R.string._1_hour)
        Interval.HOUR_2 -> stringResource(R.string._2_hours)
        Interval.HOUR_4 -> stringResource(R.string._4_hours)
        Interval.HOUR_8 -> stringResource(R.string._8_hours)
        Interval.HOUR_24 -> stringResource(R.string._24_hours)
    }
}