package me.heckfyxe.mihome.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import timber.log.Timber

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    fun json() = Json {
        ignoreUnknownKeys = true
    }

    @Provides
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