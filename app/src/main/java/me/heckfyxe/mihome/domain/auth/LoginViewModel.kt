package me.heckfyxe.mihome.domain.auth

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.heckfyxe.mihome.data.repository.AuthRepository
import me.heckfyxe.mihome.service.LongPollingForegroundService
import org.koin.core.annotation.KoinViewModel
import timber.log.Timber
import kotlin.time.Clock
import kotlin.time.Instant

@KoinViewModel
class LoginViewModel(private val repository: AuthRepository) : ViewModel() {
    val state: StateFlow<LoginUiState>
        field = MutableStateFlow<LoginUiState>(LoginUiState.Loading)

    private var longPollingStartInstant: Instant? = null
    private var longPollingJob: Job? = null

    init {
        viewModelScope.launch { loadLoginUrl() }
    }

    fun loadAgain() = viewModelScope.launch {
        state.update {
            when (it) {
                LoginUiState.LoadingError -> LoginUiState.Loading
                is LoginUiState.RefreshError -> LoginUiState.Refreshing(it.oldData)
                else -> return@launch
            }
        }
        loadLoginUrl()
    }

    fun openInBrowser(context: Context) {
        val state = state.value
        if (state !is LoginUiState.Loaded) return
        longPollingJob?.cancel()
        val timePassed = Clock.System.now() - (longPollingStartInstant ?: Clock.System.now())
        ContextCompat.startForegroundService(
            context,
            LongPollingForegroundService.createIntent(context, state.data.pollingUrl, state.data.timeout - timePassed)
        )
        val intent = CustomTabsIntent.Builder().build()
        intent.launchUrl(context, state.data.loginUrl.toUri())
    }

    private suspend fun loadLoginUrl() {
        try {
            val data = withContext(Dispatchers.IO) { repository.getLoginUrl() }
            state.value = LoginUiState.Loaded(data)
            longPollingStartInstant = Clock.System.now()
            longPollingJob = viewModelScope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        repository.startLongPolling(data.pollingUrl, data.timeout)
                    }
                } catch (_: TimeoutCancellationException) {
                    state.value = LoginUiState.Refreshing(data)
                    loadLoginUrl()
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            state.update {
                if (it is LoginUiState.Refreshing)
                    LoginUiState.RefreshError(it.oldData)
                else
                    LoginUiState.LoadingError
            }
        }
    }
}