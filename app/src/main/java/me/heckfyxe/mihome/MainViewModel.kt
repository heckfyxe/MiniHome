package me.heckfyxe.mihome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import me.heckfyxe.mihome.data.local.database.dao.AccountDao
import me.heckfyxe.mihome.data.local.database.entities.Account
import me.heckfyxe.mihome.data.model.XiaomiCountry
import me.heckfyxe.mihome.di.UserScope
import org.koin.core.annotation.KoinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.getScopeId

sealed class MainActivityUiState {
    object Loading : MainActivityUiState()
    object Unauthenticated : MainActivityUiState()
    data class LoggedIn(val account: Account) : MainActivityUiState()
}

@KoinViewModel
class MainViewModel(accountDao: AccountDao) : ViewModel(), KoinComponent {
    val state = accountDao.getAccountFlow()
        .flowOn(Dispatchers.IO)
        .map { if (it != null) MainActivityUiState.LoggedIn(it) else MainActivityUiState.Unauthenticated }
        .distinctUntilChanged()
        .onEach {
            when (it) {
                MainActivityUiState.Loading -> {}
                MainActivityUiState.Unauthenticated -> getKoin().getScopeOrNull(UserScope.getScopeId())?.close()
                is MainActivityUiState.LoggedIn -> {
                    val scope = getKoin().getOrCreateScope<UserScope>(UserScope.getScopeId())
                    scope.declare(it.account)
                    scope.declare(XiaomiCountry.Germany)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), MainActivityUiState.Loading)
}