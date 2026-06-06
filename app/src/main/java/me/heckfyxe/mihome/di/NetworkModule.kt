package me.heckfyxe.mihome.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.modules.SerializersModule
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
        serializersModule = SerializersModule {
            contextual(Any::class, AnySerializer)
        }
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

object AnySerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Any) {
        val jsonEncoder = encoder as? JsonEncoder ?: throw IllegalArgumentException("Only JSON supported")
        jsonEncoder.encodeJsonElement(value.toJsonElement())
    }

    override fun deserialize(decoder: Decoder): Any {
        val jsonDecoder = decoder as? JsonDecoder ?: throw IllegalArgumentException("Only JSON supported")
        return jsonDecoder.decodeJsonElement().toAny()
    }

    private fun Any?.toJsonElement(): JsonElement = when (this) {
        null -> JsonNull
        is JsonElement -> this
        is Boolean -> JsonPrimitive(this)
        is Number -> JsonPrimitive(this)
        is String -> JsonPrimitive(this)
        is Map<*, *> -> buildJsonObject {
            forEach { (k, v) -> put(k.toString(), v.toJsonElement()) }
        }

        is List<*> -> buildJsonArray {
            forEach { add(it.toJsonElement()) }
        }

        else -> throw IllegalArgumentException("Unsupported type: ${this::class}")
    }

    private fun JsonElement.toAny(): Any = when (this) {
        is JsonNull -> Unit
        is JsonPrimitive -> {
            if (isString) content
            else {
                content.toBooleanStrictOrNull() ?: content.toDoubleOrNull() ?: content
            }
        }

        is JsonObject -> mapValues { it.value.toAny() }
        is JsonArray -> map { it.toAny() }
    }
}
