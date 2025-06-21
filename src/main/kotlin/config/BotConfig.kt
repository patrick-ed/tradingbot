package main.config

data class BotConfig(
    val mode: Mode,
    val alpacaBaseUrl: String,
    val tickDelayMs: Long,
    val symbolsToTrade: List<String>
)

enum class Mode {
  LIVE,
  PAPER
}
