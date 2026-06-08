package me.heckfyxe.mihome.domain.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.heckfyxe.mihome.data.local.database.entities.Device
import me.heckfyxe.mihome.data.repository.DeviceRepository
import me.heckfyxe.mihome.di.DeviceScope
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Scope

@Scope(DeviceScope::class)
@KoinViewModel
class DeviceViewModel(
    @Provided device: Device,
    private val repository: DeviceRepository,
) : ViewModel() {
    val device = repository.getDeviceFlow(device.id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), device)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setProperty(2, 1, true) // Turn on a device
        }
    }
}