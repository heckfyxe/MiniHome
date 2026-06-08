package me.heckfyxe.mihome.domain.device

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.heckfyxe.mihome.data.local.database.entities.Device
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DeviceScreen(viewModel: DeviceViewModel = koinViewModel()) {
    val device by viewModel.device.collectAsStateWithLifecycle()

    DeviceScreenContent(device)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeviceScreenContent(device: Device) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(device.name) }
            )
        }
    ) { contentPadding ->
        Box(Modifier.padding(contentPadding))
    }
}