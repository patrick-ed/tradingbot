package main.config

import org.slf4j.LoggerFactory

object ApiConfig {

    val alpacaCredentials = AlpacaCredentials

    val alpacaPaperUrl: String = DotenvConfig.require("APCA_PAPER_URL")
    val alpacaLiveUrl: String = DotenvConfig.require("APCA_LIVE_URL")

    init {
        LoggerFactory.getLogger("ApiConfig").info("ApiConfig initialized")
    }
}

object AlpacaCredentials {
    val key: String = DotenvConfig.require("APCA_API_KEY_ID")
    val secret: String = DotenvConfig.require("APCA_API_SECRET_KEY")
}