package me.heckfyxe.mihome.data.repository

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.serialization.json.Json
import me.heckfyxe.mihome.data.model.AccountData
import me.heckfyxe.mihome.data.model.LoginData
import me.heckfyxe.mihome.data.remote.XiaomiService
import me.heckfyxe.mihome.util.toModel
import javax.inject.Inject
import kotlin.time.Duration

@ViewModelScoped
class AuthRepository @Inject constructor(
    private val xiaomiService: XiaomiService,
    private val serializer: Json,
) {
    suspend fun getLoginUrl(): LoginData =
        xiaomiService.getLoginUrlAndQrCode().toModel(serializer)

    suspend fun startLongPolling(url: String, timeout: Duration) {
        val accountData: AccountData = xiaomiService.startLongPolling(url, timeout)
            .toModel(serializer)
        val serviceToken = xiaomiService.getServiceToken(accountData.location)
    }
}