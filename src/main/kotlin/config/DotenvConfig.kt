package main.config

import io.github.cdimascio.dotenv.dotenv

object DotenvConfig {
    private val dotenvInstance = dotenv {
        ignoreIfMissing = true
    }

    fun get(key: String): String? = dotenvInstance[key]

    fun get(key: String, default: String): String = dotenvInstance[key] ?: default

    @Throws(IllegalStateException::class)
    fun require(key: String): String = dotenvInstance[key]
        ?.takeIf { it.isNotBlank() }
        ?: throw IllegalStateException("Required environment variable '$key' is missing or blank")
}
