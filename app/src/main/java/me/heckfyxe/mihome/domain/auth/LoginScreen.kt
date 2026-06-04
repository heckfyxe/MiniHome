package me.heckfyxe.mihome.domain.auth

import androidx.activity.compose.LocalActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel = koinViewModel()) {
    val activity = LocalActivity.current

    val qrLink by viewModel.qrLink.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val didTimeout by viewModel.didTimeout.collectAsStateWithLifecycle()

    LoginScreenContent(
        qrLink = qrLink,
        isLoading = isLoading,
        didTimeout = didTimeout,
        onLogin = { viewModel.login(false) },
        onLoginWithQrCode = { viewModel.login(true) }
    )

    LaunchedEffect(activity) {
        viewModel.openLoginPage.collect {
            val intent = CustomTabsIntent.Builder().build()
            activity?.let { activity -> intent.launchUrl(activity, it.toUri()) }
        }
    }
}

@Composable
fun LoginScreenContent(
    qrLink: String?,
    isLoading: Boolean,
    didTimeout: Boolean,
    onLogin: () -> Unit,
    onLoginWithQrCode: () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(Modifier.size(64.dp))
                }
            }

            qrLink != null -> {
                QrScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    qrLink = qrLink,
                    didTimeout = didTimeout,
                    backToLogin = onLoginWithQrCode,
                )
            }

            else -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onLogin) {
                        Text("Login With Browser")
                    }

                    Button(onLoginWithQrCode) {
                        Text("Login With QR code")
                    }
                }
            }
        }
    }


}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreenContent(
        null,
        isLoading = false,
        didTimeout = false,
        onLogin = {},
        onLoginWithQrCode = {}
    )
}
