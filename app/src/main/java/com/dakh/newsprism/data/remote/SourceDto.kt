package com.dakh.newsprism.data.remote

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Serializable
data class SourceDto(
    @SerialName("name")
    val name: String = "",
)