package main.config

import kotlinx.serialization.json.Json

object JsonConfig {
    val get = Json { ignoreUnknownKeys = true }
}
