package main.strategy

import main.models.Bar
import main.models.Position

// Data class to hold all the necessary context for a strategy to make a decision
data class StrategyContext(
    val symbol: String,
    val bars: List<Bar>,
    val currentPosition: Position?,
    val buyingPower: Double
)

// The output of a strategy evaluation
sealed class Signal {
  object HOLD : Signal()

  data class BUY(val symbol: String) : Signal()

  data class SELL(val symbol: String) : Signal()
}

// The interface that all trading strategies must implement
interface TradingStrategy {
  // The name of the strategy for logging
  val name: String

  // The main evaluation function
  suspend fun evaluate(context: StrategyContext): Signal
}
