package me.heckfyxe.mihome.domain.auth

import androidx.compose.runtime.Immutable
import me.heckfyxe.mihome.data.model.LoginData

@Immutable
sealed class LoginUiState {
    object Loading: LoginUiState()
    data class Loaded(val data: LoginData): LoginUiState()
    data class Refreshing(val oldData: LoginData): LoginUiState()
    object LoadingError: LoginUiState()
    data class RefreshError(val oldData: LoginData): LoginUiState()
}