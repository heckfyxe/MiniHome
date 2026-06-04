package me.heckfyxe.mihome.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.setCookie
import io.ktor.util.appendAll
import org.koin.core.annotation.Single
import java.util.Date
import kotlin.time.Duration

@Single
class XiaomiAuthApi(private val client: HttpClient) {
    suspend fun getLoginUrlAndQrCode() =
        client.get("https://account.xiaomi.com/longPolling/loginUrl") {
            url.parameters.appendAll(
                "_qrsize" to "480",
                "qs" to "%3Fsid%3Dxiaomiio%26_json%3Dtrue",
                "callback" to "https://sts.api.io.mi.com/sts",
                "_hasLogo" to "false",
                "sid" to "xiaomiio",
                "serviceParam" to "",
                "_locale" to "en_GB",
                "_dc" to Date().time.toString(),
            )
        }

    suspend fun startLongPolling(url: String, timeout: Duration) = client.get {
        url(url)
        timeout {
            requestTimeoutMillis = timeout.inWholeMilliseconds
        }
    }

    suspend fun getServiceToken(location: String) = client.get(location) {
        header("Content-Type", "application/x-www-form-urlencoded")
    }.setCookie().single { it.name == "serviceToken" }.value
}
