package main

import kotlinx.coroutines.runBlocking
import main.alpaca.AlpacaApi
import main.config.ApiConfig
import main.config.BotConfig
import main.config.DatabaseConfig
import main.config.Mode
import main.config.Mode.PAPER
import main.config.Mode.LIVE
import org.slf4j.LoggerFactory

fun main(args: Array<String>) = runBlocking {
    val logger = LoggerFactory.getLogger("MainKt")
    logger.info("Bot starting...")

    val apiConfig = ApiConfig
    val databaseConfig = DatabaseConfig

    val mode: Mode = PAPER
    val botConfig = when(mode){
        PAPER -> BotConfig(PAPER,apiConfig.alpacaPaperUrl)
        LIVE -> BotConfig(LIVE,apiConfig.alpacaLiveUrl)
    }

    val alpacaApi = AlpacaApi(botConfig, apiConfig)
    println(alpacaApi.getAccount())


}