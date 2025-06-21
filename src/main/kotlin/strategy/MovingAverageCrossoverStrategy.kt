package main.strategy

import org.slf4j.LoggerFactory

class MovingAverageCrossoverStrategy(
    private val shortWindow: Int = 20, // e.g., 20 periods
    private val longWindow: Int = 50, // e.g., 50 periods
    private val tradeAmountUsd: Double = 100.00 // How much to buy in USD
) : TradingStrategy {

  override val name = "MovingAverageCrossover(short=$shortWindow, long=$longWindow)"
  private val logger = LoggerFactory.getLogger(javaClass)

  override suspend fun evaluate(context: StrategyContext): Signal {
    val closePrices = context.bars.map { it.close }

    // We need enough data to calculate the long window moving average
    if (closePrices.size < longWindow) {
      logger.info(
          "[${context.symbol}] Not enough data to evaluate strategy, need $longWindow bars but have ${closePrices.size}.")
      return Signal.HOLD
    }

    // Calculate moving averages for the last two time points
    val lastShortMA = closePrices.takeLast(shortWindow).average()
    val prevShortMA = closePrices.dropLast(1).takeLast(shortWindow).average()

    val lastLongMA = closePrices.takeLast(longWindow).average()
    val prevLongMA = closePrices.dropLast(1).takeLast(longWindow).average()

    logger.info(
        "[${context.symbol}] MAs -> Short: %.2f (prev: %.2f) | Long: %.2f (prev: %.2f)"
            .format(lastShortMA, prevShortMA, lastLongMA, prevLongMA))

    // --- Crossover Logic ---

    // BUY signal: short MA crosses above long MA
    val isCrossUp = prevShortMA <= prevLongMA && lastShortMA > lastLongMA
    if (isCrossUp && context.currentPosition == null) {
      if (context.buyingPower > tradeAmountUsd) {
        logger.info("[${context.symbol}] BUY SIGNAL DETECTED (Cross Up)")
        return Signal.BUY(context.symbol)
      } else {
        logger.warn(
            "[${context.symbol}] BUY SIGNAL DETECTED but not enough buying power. Required: $tradeAmountUsd, Have: ${context.buyingPower}")
      }
    }

    // SELL signal: short MA crosses below long MA
    val isCrossDown = prevShortMA >= prevLongMA && lastShortMA < lastLongMA
    if (isCrossDown && context.currentPosition != null) {
      logger.info("[${context.symbol}] SELL SIGNAL DETECTED (Cross Down)")
      return Signal.SELL(context.symbol)
    }

    // Otherwise, do nothing
    return Signal.HOLD
  }
}
