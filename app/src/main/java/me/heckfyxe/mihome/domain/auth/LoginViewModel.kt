package me.heckfyxe.mihome.domain.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {
    val openLoginPage: SharedFlow<String>
        field = MutableSharedFlow<String>()

    val qrCode: StateFlow<String?>
        field = MutableStateFlow<String?>(null)

    fun login(useQR: Boolean) = viewModelScope.launch {
        val data = withContext(Dispatchers.IO) { repository.getLoginUrl() }
        startPolling(data)

        if (useQR) qrCode.value = data.qr
        else openLoginPage.emit(data.loginUrl)
    }

    private fun startPolling(data: LoginData) = viewModelScope.launch {
        val response = withContext(Dispatchers.IO) {
            repository.startLongPolling(
                data.pollingUrl,
                data.timeout
            )
        }
    }
}