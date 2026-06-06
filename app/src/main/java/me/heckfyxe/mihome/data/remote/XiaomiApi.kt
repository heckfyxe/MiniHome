package me.heckfyxe.mihome.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.cookie
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.util.appendAll
import kotlinx.serialization.json.Json
import me.heckfyxe.mihome.crypto.decryptRC4
import me.heckfyxe.mihome.crypto.encryptRC4
import me.heckfyxe.mihome.crypto.generateNonce
import me.heckfyxe.mihome.crypto.signNonce
import me.heckfyxe.mihome.data.local.database.entities.Account
import me.heckfyxe.mihome.data.model.ApiResponse
import me.heckfyxe.mihome.data.model.DevicesResult
import me.heckfyxe.mihome.data.model.HomeResult
import me.heckfyxe.mihome.data.model.XiaomiCountry
import me.heckfyxe.mihome.di.UserScope
import okio.ByteString.Companion.encode
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scope(UserScope::class)
@Scoped
class XiaomiApi(
    private val json: Json,
    private val client: HttpClient,
    @Provided private val account: Account,
    @Provided private val country: XiaomiCountry,
) {
    private val userId = account.userId.toString()
    private val ssecurity = account.ssecurity
    private val serviceToken = account.serviceToken

    private val agent: String by lazy {
        val agentId = String(CharArray(13) { (65..69).random().toChar() })
        val randomText = String(CharArray(18) { (97..122).random().toChar() })
        "$randomText-$agentId APP/com.xiaomi.mihome APPV/10.5.201"
    }

    suspend fun getHomes(): ApiResponse<HomeResult> = apiCall(
        "/v2/homeroom/gethome", mapOf(
            "data" to json.encodeToString(
                mapOf(
                    "fg" to true,
                    "fetch_share" to true,
                    "fetch_share_dev" to true,
                    "limit" to 300,
                    "app_ver" to 7
                )
            )
        )
    )

    suspend fun getDevices(ownerId: String, homeId: String): ApiResponse<DevicesResult> = apiCall(
        "/v2/home/home_device_list", mapOf(
            "data" to json.encodeToString(
                mapOf(
                    "home_owner" to ownerId,
                    "home_id" to homeId,
                    "limit" to 200,
                    "get_split_device" to true,
                    "support_smart_home" to true,
                )
            )
        )
    )

    private suspend inline fun <reified T> apiCall(url: String, params: Map<String, String>): T {
        val nonce = generateNonce()
        val signedNonce = signNonce(ssecurity, nonce)
        val params = params.toMutableMap()
        val fields = generateEncParams(url, signedNonce, nonce, params)
        val response = client.post(country.apiUrl + url) {
            this.url.parameters.appendAll(fields)
            headers {
                appendAll(
                    "Accept-Encoding" to "identity",
                    "User-Agent" to agent,
                    "Content-Type" to "application/x-www-form-urlencoded",
                    "x-xiaomi-protocal-flag-cli" to "PROTOCAL-HTTP2",
                    "MIOT-ENCRYPT-ALGORITHM" to "ENCRYPT-RC4",
                )
            }
            mapOf(
                "userId" to userId,
                "yetAnotherServiceToken" to serviceToken,
                "serviceToken" to serviceToken,
                "locale" to "en_GB",
                "timezone" to "GMT+02:00",
                "is_daylight" to "1",
                "dst_offset" to "3600000",
                "channel" to "MI_APP_STORE",
            ).forEach(::cookie)
        }
        return json.decodeFromString(decryptRC4(signedNonce, response.bodyAsText()))
    }

    private fun generateEncSignature(
        url: String,
        signedNonce: String,
        params: Map<String, String>,
    ): String {
        val signatureParams = buildList {
            add("POST")
            add(url)
            params.forEach { (k, v) -> add("$k=$v") }
            add(signedNonce)
        }
        val signature = signatureParams.joinToString("&")
        return signature.encode().sha1().base64()
    }

    private fun generateEncParams(
        url: String,
        signedNonce: String,
        nonce: String,
        params: MutableMap<String, String>,
    ): Map<String, String> {
        params["rc4_hash__"] = generateEncSignature(url, signedNonce, params)
        params.replaceAll { _, value -> encryptRC4(signedNonce, value) }
        params["signature"] = generateEncSignature(url, signedNonce, params)
        params["ssecurity"] = ssecurity
        params["_nonce"] = nonce
        return params
    }
}

private val XiaomiCountry.apiUrl: String
    get() = "https://${if (this == XiaomiCountry.China) "" else "$iso."}api.io.mi.com/app"