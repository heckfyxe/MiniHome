package me.heckfyxe.mihome.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.cookie
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.util.appendAll
import me.heckfyxe.mihome.crypto.decryptRC4
import me.heckfyxe.mihome.crypto.encryptRC4
import me.heckfyxe.mihome.crypto.generateNonce
import me.heckfyxe.mihome.crypto.signNonce
import me.heckfyxe.mihome.data.local.database.entities.Account
import me.heckfyxe.mihome.data.model.XiaomiCountry
import me.heckfyxe.mihome.di.UserScope
import okio.ByteString.Companion.encode
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scope(UserScope::class)
@Scoped
class XiaomiApi(private val client: HttpClient, @Provided account: Account) {
    private val userId = account.userId.toString()
    private val ssecurity = account.ssecurity
    private val serviceToken = account.serviceToken

    private val agent: String by lazy {
        val agentId = String(CharArray(13) { (65..69).random().toChar() })
        val randomText = String(CharArray(18) { (97..122).random().toChar() })
        "$randomText-$agentId APP/com.xiaomi.mihome APPV/10.5.201"
    }

    suspend fun getHomes(country: XiaomiCountry) = apiCall(
        "${country.apiUrl}/v2/homeroom/gethome", mapOf(
            "data" to "{\"fg\": true, \"fetch_share\": true, \"fetch_share_dev\": true, \"limit\": 300, \"app_ver\": 7}"
        )
    )


    private suspend fun apiCall(url: String, params: Map<String, String>): String {
        val nonce = generateNonce()
        val signedNonce = signNonce(ssecurity, nonce)
        val params = params.toMutableMap()
        val fields = generateEncParams(url, "POST", signedNonce, nonce, params)
        val response = client.post(url) {
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
        return decryptRC4(signedNonce, response.bodyAsText())
    }

    private fun generateEncSignature(
        url: String,
        method: String,
        signedNonce: String,
        params: Map<String, String>,
    ): String {
        val signatureParams = buildList {
            add(method.uppercase())
            add(url.split("com")[1].replace("/app/", "/"))
            params.forEach { (k, v) -> add("$k=$v") }
            add(signedNonce)
        }
        val signature = signatureParams.joinToString("&")
        return signature.encode().sha1().base64()
    }

    private fun generateEncParams(
        url: String,
        method: String,
        signedNonce: String,
        nonce: String,
        params: MutableMap<String, String>,
    ): Map<String, String> {
        params["rc4_hash__"] = generateEncSignature(url, method, signedNonce, params)
        params.replaceAll { _, value -> encryptRC4(signedNonce, value) }
        params["signature"] = generateEncSignature(url, method, signedNonce, params)
        params["ssecurity"] = ssecurity
        params["_nonce"] = nonce
        return params
    }
}

private val XiaomiCountry.apiUrl: String
    get() = "https://${if (this == XiaomiCountry.China) "" else "$iso."}api.io.mi.com/app"