package main

import kotlin.concurrent.thread
import kotlinx.coroutines.runBlocking
import main.alpaca.AlpacaApi
import main.bot.TradingBot
import main.config.ApiConfig
import main.config.BotConfig
import main.config.DatabaseConfig
import main.config.Mode
import main.config.Mode.LIVE
import main.config.Mode.PAPER
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("MainKt")
private val apiConfig = ApiConfig
private val databaseConfig = DatabaseConfig
private const val TICK_DELAY_MS = 5000L

fun main(args: Array<String>) = runBlocking {
  // Configuration
  val mode: Mode = PAPER
  val botConfig =
      when (mode) {
        PAPER -> BotConfig(PAPER, apiConfig.alpacaPaperUrl, TICK_DELAY_MS)
        LIVE -> BotConfig(LIVE, apiConfig.alpacaLiveUrl, TICK_DELAY_MS)
      }

  // Dependency Injection
  val alpacaApi = AlpacaApi(botConfig, apiConfig)
  val tradingBot = TradingBot(alpacaApi, botConfig)

  // Lifecycle Management
  logger.info("Application starting...")
  logger.info("MODE: $mode")

  // Register a hook to stop the bot when the JVM is terminated
  Runtime.getRuntime()
      .addShutdownHook(
          thread(start = false) {
            logger.info("Shutdown signal received. Stopping bot...")
            runBlocking { tradingBot.stop() }
            logger.info("Application has shut down.")
          })

  tradingBot.start(this)

  logger.info("Application is running. Press Ctrl+C to exit.")
}
