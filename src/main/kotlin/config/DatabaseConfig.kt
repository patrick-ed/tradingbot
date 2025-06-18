package main.config

import org.slf4j.LoggerFactory

object DatabaseConfig {

    val dbUrl: String = DotenvConfig.require("DB_URL")
    val dbUser: String = DotenvConfig.require("DB_USER")
    val dbPassword: String = DotenvConfig.require("DB_PASSWORD")

    init {
        LoggerFactory.getLogger("DatabaseConfig").info("DatabaseConfig initialized")
    }
}
