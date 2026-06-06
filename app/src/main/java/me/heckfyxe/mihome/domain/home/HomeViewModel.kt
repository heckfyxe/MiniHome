package me.heckfyxe.mihome.domain.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.heckfyxe.mihome.data.repository.HomeRepository
import me.heckfyxe.mihome.di.UserScope
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Scope
import timber.log.Timber

@Scope(UserScope::class)
@KoinViewModel
class HomeViewModel(private val repository: HomeRepository) : ViewModel() {
    init {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                repository.getHomes().toString().also(Timber::d)
                repository.getDevices()
            }.onFailure(Timber::e)
        }
    }

    val homes = repository.getHomesWithRoomsAndDevicesFlow()

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.logout()
        }
    }
}