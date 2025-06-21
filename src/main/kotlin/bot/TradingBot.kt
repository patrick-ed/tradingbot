package main.bot

import kotlinx.coroutines.*
import main.alpaca.AlpacaApi
import main.config.BotConfig
import org.slf4j.LoggerFactory

class TradingBot(private val alpacaApi: AlpacaApi, private val botConfig: BotConfig) {

  private val logger = LoggerFactory.getLogger(TradingBot::class.java)
  private lateinit var botJob: Job

  fun start(scope: CoroutineScope) {
    logger.info("Starting TradingBot...")
    botJob =
        scope.launch {
          try {
            logger.info("================= BOT RUNNING =================")
            while (isActive) {
              onTick()
              delay(botConfig.tickDelayMs)
            }
          } finally {
            handleShutdown()
          }
        }
  }

  suspend fun stop() {
    logger.info("Stopping TradingBot...")
    botJob.cancelAndJoin()
    logger.info("TradingBot has been stopped.")
  }

  private suspend fun onTick() {
    val account = alpacaApi.getAccount()
    logger.info(account.toString())
  }

  private fun handleShutdown() {
    logger.info("Cleaning up resources...")
    // Handle shutting bot down here
    // e.g. closing open trades, saving states
  }
}
