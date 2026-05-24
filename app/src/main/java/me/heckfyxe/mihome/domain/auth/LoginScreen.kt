package me.heckfyxe.mihome.domain.auth

import androidx.activity.compose.LocalActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun LoginScreen() {
    val viewModel: LoginViewModel = hiltViewModel()
    val activity = LocalActivity.current

    val qrCode by viewModel.qrCode.collectAsStateWithLifecycle()

    LoginScreenContent(
        qrCode,
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
    qrCode: String?,
    onLogin: () -> Unit,
    onLoginWithQrCode: () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        if (qrCode != null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                val context = LocalContext.current
                val painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(qrCode)
                        .crossfade(true)
                        .build()
                )
                val state by painter.state.collectAsState()

                when (state) {
                    is AsyncImagePainter.State.Empty,
                    is AsyncImagePainter.State.Loading -> {
                        CircularProgressIndicator(Modifier.size(64.dp))
                    }

                    is AsyncImagePainter.State.Success -> {
                        Image(
                            modifier = Modifier.aspectRatio(1f),
                            painter = painter,
                            contentDescription = "QR code"
                        )
                    }

                    is AsyncImagePainter.State.Error -> {
                        Text("Error")
                    }
                }
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onLogin) {
                    Text("Login")
                }

                Button(onLoginWithQrCode) {
                    Text("Login With QR code")
                }
            }
        }
    }


}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreenContent(null, {}, {})
}
