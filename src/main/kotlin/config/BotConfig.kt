package main.config

data class BotConfig(val mode: Mode, val alpacaBaseUrl: String)

enum class Mode{LIVE,PAPER}