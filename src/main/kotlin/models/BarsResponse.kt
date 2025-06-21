package main.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BarsResponse(
    val bars: List<Bar>? = null, // Changed: Added '?' and default value
    val symbol: String,
    @SerialName("next_page_token") val nextPageToken: String? = null
)
