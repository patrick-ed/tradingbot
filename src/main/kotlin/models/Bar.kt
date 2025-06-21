package main.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Bar(
    @SerialName("t") val timestamp: String,
    @SerialName("o") val open: Double,
    @SerialName("h") val high: Double,
    @SerialName("l") val low: Double,
    @SerialName("c") val close: Double,
    @SerialName("v") val volume: Long
)
