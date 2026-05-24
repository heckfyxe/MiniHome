package me.heckfyxe.mihome.util

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

suspend inline fun <reified T> HttpResponse.toModel(json: Json = Json) =
    json.decodeFromString<T>(this.bodyAsText().removePrefix("&&&START&&&"))