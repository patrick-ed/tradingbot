package main.models

import kotlinx.serialization.Serializable

@Serializable data class OrderResponse(val id: String, val symbol: String, val status: String)
