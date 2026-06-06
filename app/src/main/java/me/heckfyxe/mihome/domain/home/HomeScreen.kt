package me.heckfyxe.mihome.domain.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import timber.log.Timber

@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Home")
            ElevatedButton(viewModel::logout) {
                Text("Logout")
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.homes.collect { Timber.d(it.toString()) }
    }
}