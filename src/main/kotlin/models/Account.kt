package main.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val id: String,
    @SerialName("account_number") val accountNumber: String,
    val status: String,
    val currency: String,
    @SerialName("buying_power") val buyingPower: String,
    val equity: String
)
