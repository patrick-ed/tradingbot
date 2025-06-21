package main.config

data class BotConfig(val mode: Mode, val alpacaBaseUrl: String, val tickDelayMs: Long)

enum class Mode{LIVE,PAPER}