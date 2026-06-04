package me.heckfyxe.mihome.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton
import timber.log.Timber

@Module
@Configuration
class NetworkModule {
    @Singleton
    fun json() = Json {
        ignoreUnknownKeys = true
    }

    @Singleton
    fun httpClient(json: Json) = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(json = json)
        }
        install(ContentEncoding) {
            gzip()
            deflate()
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Timber.tag("Ktor").v(message)
                }
            }
            level = LogLevel.ALL
        }
    }
}