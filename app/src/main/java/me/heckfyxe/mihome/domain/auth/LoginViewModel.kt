package me.heckfyxe.mihome.domain.auth

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.SingletonImageLoader
import coil3.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.heckfyxe.mihome.data.repository.AuthRepository
import me.heckfyxe.mihome.service.LongPollingForegroundService
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class LoginViewModel(
    private val context: Application,
    private val repository: AuthRepository,
) : ViewModel() {
    val openLoginPage: SharedFlow<String>
        field = MutableSharedFlow()

    val displayErrorMessage: SharedFlow<Unit>
        field = MutableSharedFlow()

    val isLoading: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val qrLink: StateFlow<String?>
        field = MutableStateFlow(null)

    val didTimeout: StateFlow<Boolean>
        field = MutableStateFlow(false)

    fun login(useQR: Boolean) = viewModelScope.launch {
        isLoading.value = true
        val data = try {
            withContext(Dispatchers.IO) { repository.getLoginUrl() }
        } catch (_: Exception) {
            isLoading.value = false
            displayErrorMessage.emit(Unit)
            return@launch
        }
        ContextCompat.startForegroundService(
            context,
            LongPollingForegroundService.createIntent(context, data.pollingUrl, data.timeout)
        )

        if (useQR) {
            SingletonImageLoader.get(context)
                .execute(
                    ImageRequest.Builder(context)
                        .data(data.qr)
                        .build()
                )
        }

        isLoading.value = false
        if (useQR) qrLink.value = data.qr
        else openLoginPage.emit(data.loginUrl)
    }
}