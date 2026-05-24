package me.heckfyxe.mihome.domain.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.SingletonImageLoader
import coil3.request.ImageRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.heckfyxe.mihome.data.model.LoginData
import me.heckfyxe.mihome.data.repository.AuthRepository
import java.util.concurrent.TimeoutException

@HiltViewModel
class LoginViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val repository: AuthRepository
) : ViewModel() {
    val openLoginPage: SharedFlow<String>
        field = MutableSharedFlow<String>()

    val displayErrorMessage: SharedFlow<Unit>
        field = MutableSharedFlow<Unit>()

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
        startPolling(data)

        SingletonImageLoader.get(context)
            .execute(
                ImageRequest.Builder(context)
                    .data(data.qr)
                    .build()
            )

        isLoading.value = false
        if (useQR) qrLink.value = data.qr
        else openLoginPage.emit(data.loginUrl)
    }

    private fun startPolling(data: LoginData) = viewModelScope.launch {
        try {
            withContext(Dispatchers.IO) {
                repository.startLongPolling(
                    data.pollingUrl,
                    data.timeout
                )
            }
        } catch (_: TimeoutException) {
            didTimeout.value = true
        } catch (_: Exception) {
            displayErrorMessage.emit(Unit)
        }
    }
}