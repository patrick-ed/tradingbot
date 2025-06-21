package main.alpaca

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import main.config.ApiConfig
import main.config.BotConfig
import main.config.JsonConfig
import main.models.*
import org.slf4j.LoggerFactory

class AlpacaApi(private val botConfig: BotConfig, private val apiConfig: ApiConfig) {

  private val logger = LoggerFactory.getLogger(javaClass)
  private val dataUrl = "https://data.alpaca.markets"

  private val client =
      HttpClient(CIO) {
        install(ContentNegotiation) { json(JsonConfig.get) }
        install(Logging) { level = LogLevel.NONE }
        defaultRequest {
          header("APCA-API-KEY-ID", apiConfig.alpacaCredentials.key)
          header("APCA-API-SECRET-KEY", apiConfig.alpacaCredentials.secret)
        }
        expectSuccess = false
      }

  suspend fun getAccount(): Account {
    logger.info("Fetching account information...")
    val response = client.get("${botConfig.alpacaBaseUrl}/v2/account")
    return handleResponse(response)
  }

  // UPDATED: This function now safely handles a null 'bars' list from the API
  suspend fun getBars(symbol: String, timeframe: String = "1Day", limit: Int = 100): List<Bar> {
    logger.info("Fetching $limit bars for $symbol...")
    val response =
        client.get("$dataUrl/v2/stocks/$symbol/bars") {
          parameter("timeframe", timeframe)
          parameter("limit", limit)
          parameter("feed", "iex") // Use IEX for free data
        }

    val barsResponse: BarsResponse = handleResponse(response)

    // If API returns "bars": null, we convert it to an empty list
    // so the rest of the application doesn't have to worry about nulls.
    return barsResponse.bars ?: emptyList()
  }

  suspend fun getPositions(): List<Position> {
    logger.info("Fetching open positions...")
    val response = client.get("${botConfig.alpacaBaseUrl}/v2/positions")
    // It's good practice to handle potential null lists here too, just in case.
    return handleResponse<List<Position>?>(response) ?: emptyList()
  }

  suspend fun submitMarketOrder(symbol: String, qty: String, side: String): OrderResponse {
    val orderRequest =
        OrderRequest(symbol = symbol, qty = qty, side = side, type = "market", timeInForce = "day")
    logger.info("Submitting market order: $orderRequest")
    val response =
        client.post("${botConfig.alpacaBaseUrl}/v2/orders") {
          contentType(ContentType.Application.Json)
          setBody(orderRequest)
        }
    return handleResponse(response)
  }

  suspend fun closePosition(symbol: String): OrderResponse {
    logger.info("Closing position for $symbol...")
    val response = client.delete("${botConfig.alpacaBaseUrl}/v2/positions/$symbol")
    return handleResponse(response)
  }

  suspend fun closeAllPositions(): List<OrderResponse> {
    logger.warn("CLOSING ALL OPEN POSITIONS!")
    val response = client.delete("${botConfig.alpacaBaseUrl}/v2/positions")
    return handleResponse<List<OrderResponse>?>(response) ?: emptyList()
  }

  private suspend inline fun <reified T> handleResponse(response: HttpResponse): T {
    if (response.status.isSuccess()) {
      return response.body<T>()
    } else {
      val errorBody = response.body<String>()
      logger.error("API Error: ${response.status}. Body: $errorBody")
      throw IllegalStateException("API call failed with status ${response.status}: $errorBody")
    }
  }
}
