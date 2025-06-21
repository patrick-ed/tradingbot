package main.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Position(
    @SerialName("asset_id") val assetId: String,
    val symbol: String,
    val exchange: String,
    @SerialName("asset_class") val assetClass: String,
    @SerialName("avg_entry_price") val avgEntryPrice: String,
    val qty: String,
    val side: String,
    @SerialName("market_value") val marketValue: String,
    @SerialName("unrealized_pl") val unrealizedPl: String,
)
