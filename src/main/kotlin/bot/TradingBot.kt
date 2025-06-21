package main.bot

import kotlin.math.floor
import kotlinx.coroutines.*
import main.alpaca.AlpacaApi
import main.config.BotConfig
import main.strategy.Signal
import main.strategy.StrategyContext
import main.strategy.TradingStrategy
import org.slf4j.LoggerFactory

class TradingBot(
    private val alpacaApi: AlpacaApi,
    private val botConfig: BotConfig,
    private val strategies: List<TradingStrategy>
) {

  private val logger = LoggerFactory.getLogger(TradingBot::class.java)
  private lateinit var botJob: Job
  private val tradeAmountUsd = 100.00 // Example: trade $100 per buy order

  fun start(scope: CoroutineScope) {
    logger.info("Starting TradingBot with strategies: ${strategies.joinToString { it.name }}")
    botJob =
        scope.launch(Dispatchers.IO) { // Use IO dispatcher for network calls
          try {
            while (isActive) {
              logger.info("================= BOT TICK START =================")
              onTick()
              logger.info(
                  "================= BOT TICK END (waiting ${botConfig.tickDelayMs}ms) =================\n")
              delay(botConfig.tickDelayMs)
            }
          } finally {
            withContext(NonCancellable) { // Ensure shutdown logic runs even if cancelled
              handleShutdown()
            }
          }
        }
  }

  suspend fun stop() {
    logger.info("Stopping TradingBot...")
    if (::botJob.isInitialized) {
      botJob.cancelAndJoin()
    }
    logger.info("TradingBot has been stopped.")
  }

  private suspend fun onTick() {
    val account = alpacaApi.getAccount()
    val positions = alpacaApi.getPositions()
    val positionsBySymbol = positions.associateBy { it.symbol }

    for (symbol in botConfig.symbolsToTrade) {
      try {
        logger.info("--- Evaluating symbol: $symbol ---")
        val bars = alpacaApi.getBars(symbol, limit = 100)

        // UPDATED LOGIC: Check if bars are empty and provide a better reason
        if (bars.isEmpty()) {
          logger.warn(
              "Could not fetch bar data for $symbol. " +
                  "This is expected if the market is closed or the symbol is invalid.")
          continue // Skip to the next symbol
        }

        val context =
            StrategyContext(
                symbol = symbol,
                bars = bars,
                currentPosition = positionsBySymbol[symbol],
                buyingPower = account.buyingPower.toDouble())

        val signal = strategies.first().evaluate(context)
        handleSignal(signal, bars.last().close)
      } catch (e: Exception) {
        logger.error("Error processing symbol $symbol: ${e.message}", e)
      }
    }
  }

  private suspend fun handleSignal(signal: Signal, lastPrice: Double) {
    when (signal) {
      is Signal.BUY -> {
        val quantity = floor(tradeAmountUsd / lastPrice).toInt().toString()
        logger.info("EXECUTE BUY: Placing market order for $quantity shares of ${signal.symbol}")
        val order = alpacaApi.submitMarketOrder(signal.symbol, quantity, "buy")
        logger.info("Buy order submitted: ${order.id} status: ${order.status}")
      }
      is Signal.SELL -> {
        logger.info("EXECUTE SELL: Closing position for ${signal.symbol}")
        val order = alpacaApi.closePosition(signal.symbol)
        logger.info("Sell order submitted: ${order.id} status: ${order.status}")
      }
      is Signal.HOLD -> {
        // No log needed for HOLD to keep the output clean
      }
    }
  }

  private suspend fun handleShutdown() {
    logger.info("Shutting down: closing all open positions...")
    try {
      val closedOrders = alpacaApi.closeAllPositions()
      if (closedOrders.isNotEmpty()) {
        logger.info("Successfully closed ${closedOrders.size} positions.")
      } else {
        logger.info("No open positions to close.")
      }
    } catch (e: Exception) {
      logger.error("Failed to close all positions during shutdown: ${e.message}", e)
    }
    logger.info("Cleanup complete.")
  }
}
