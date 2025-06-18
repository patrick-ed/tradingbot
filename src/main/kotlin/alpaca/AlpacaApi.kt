package main.alpaca

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import main.config.ApiConfig
import main.config.BotConfig
import main.config.JsonConfig
import main.models.Account
import org.slf4j.LoggerFactory

class AlpacaApi(private val botConfig: BotConfig, private val apiConfig: ApiConfig) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(JsonConfig.get)
        }
        install(Logging) {
            level = LogLevel.INFO
        }
        // Add auth headers to every REST request
        defaultRequest {
            header("APCA-API-KEY-ID", apiConfig.alpacaCredentials.key)
            header("APCA-API-SECRET-KEY", apiConfig.alpacaCredentials.secret)
        }
    }

    suspend fun getAccount(): Account {
        logger.info("Fetching account information...")
        return client.get("${botConfig.alpacaBaseUrl}/v2/account").body()
    }
}
