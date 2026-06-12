package me.heckfyxe.mihome.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import me.heckfyxe.mihome.data.local.database.dao.AccountDao
import me.heckfyxe.mihome.data.local.database.entities.toEntity
import me.heckfyxe.mihome.data.model.AccountData
import me.heckfyxe.mihome.data.model.LoginData
import me.heckfyxe.mihome.data.remote.XiaomiAuthApi
import me.heckfyxe.mihome.util.toModel
import org.koin.core.annotation.Factory
import timber.log.Timber
import kotlin.time.Duration

@Factory
class AuthRepository(
    private val xiaomiAuthApi: XiaomiAuthApi,
    private val serializer: Json,
    private val accountDao: AccountDao,
) {
    suspend fun getLoginUrl(): LoginData =
        xiaomiAuthApi.getLoginUrlAndQrCode().toModel(serializer)

    suspend fun startLongPolling(url: String, timeout: Duration) {
        val accountData: AccountData = withTimeout(timeout) {
            while (isActive) {
                try {
                    return@withTimeout xiaomiAuthApi.startLongPolling(url, timeout)
                        .toModel(serializer)
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    Timber.e(e)
                }
            }
            error("unreachable")
        }

        val serviceToken = xiaomiAuthApi.getServiceToken(accountData.location)
        accountDao.upsert(accountData.toEntity(serviceToken))
    }
}