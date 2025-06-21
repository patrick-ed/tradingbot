package main.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    val symbol: String,
    val qty: String,
    val side: String, // "buy" or "sell"
    val type: String, // "market", "limit", etc.
    @SerialName("time_in_force") val timeInForce: String // "day", "gtc", etc.
)
