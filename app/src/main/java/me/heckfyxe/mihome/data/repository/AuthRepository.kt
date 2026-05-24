package me.heckfyxe.mihome.data.repository

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import me.heckfyxe.mihome.data.model.AccountData
import me.heckfyxe.mihome.data.model.LoginData
import me.heckfyxe.mihome.data.remote.XiaomiApi
import me.heckfyxe.mihome.util.toModel
import timber.log.Timber
import java.util.concurrent.TimeoutException
import javax.inject.Inject
import kotlin.time.Duration

@ViewModelScoped
class AuthRepository @Inject constructor(
    private val xiaomiApi: XiaomiApi,
    private val serializer: Json,
) {
    suspend fun getLoginUrl(): LoginData =
        xiaomiApi.getLoginUrlAndQrCode().toModel(serializer)

    suspend fun startLongPolling(url: String, timeout: Duration) {
        val accountData: AccountData = withTimeout(timeout) {
            while (isActive) {
                try {
                    return@withTimeout xiaomiApi.startLongPolling(url, timeout)
                        .toModel(serializer)
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
            throw TimeoutException()
        }

        val serviceToken = xiaomiApi.getServiceToken(accountData.location)
    }
}