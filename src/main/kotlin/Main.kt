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
import main.strategy.MovingAverageCrossoverStrategy
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("MainKt")
private val apiConfig = ApiConfig
private val databaseConfig = DatabaseConfig
private const val TICK_DELAY_MS = 60000L // Set to 1 minute to avoid rate limits

fun main(args: Array<String>) = runBlocking {
  // Configuration
  val mode: Mode = PAPER

  // NEW: Define which stocks to trade
  val symbolsToTrade = listOf("AAPL", "GOOG", "TSLA")

  val botConfig =
      when (mode) {
        PAPER -> BotConfig(PAPER, apiConfig.alpacaPaperUrl, TICK_DELAY_MS, symbolsToTrade)
        LIVE -> BotConfig(LIVE, apiConfig.alpacaLiveUrl, TICK_DELAY_MS, symbolsToTrade)
      }

  // Dependency Injection
  val alpacaApi = AlpacaApi(botConfig, apiConfig)

  val strategies = listOf(MovingAverageCrossoverStrategy(shortWindow = 10, longWindow = 30))
  // NEW: Inject strategies into the bot
  val tradingBot = TradingBot(alpacaApi, botConfig, strategies)

  // Lifecycle Management
  logger.info("Application starting...")
  logger.info("MODE: $mode")
  logger.info("TRADING SYMBOLS: $symbolsToTrade")

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
