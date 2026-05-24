package me.heckfyxe.mihome.domain.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun QrScreen(
    modifier: Modifier = Modifier,
    qrLink: String,
    didTimeout: Boolean,
    backToLogin: () -> Unit
) {
    val context = LocalContext.current
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(qrLink)
            .crossfade(true)
            .build()
    )
    val state by painter.state.collectAsState()

    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =
            if (didTimeout && state is AsyncImagePainter.State.Success) Arrangement.SpaceBetween
            else Arrangement.Center,
    ) {
        when (state) {
            is AsyncImagePainter.State.Empty,
            is AsyncImagePainter.State.Loading -> {
                CircularProgressIndicator(Modifier.size(64.dp))
            }

            is AsyncImagePainter.State.Success -> {
                if (didTimeout) {
                    Spacer(Modifier)
                }

                Image(
                    modifier = Modifier.aspectRatio(1f),
                    painter = painter,
                    contentDescription = "QR code"
                )

                if (didTimeout) {
                    Button(backToLogin, Modifier.fillMaxWidth()) {
                        Text("Try Again")
                    }
                }
            }

            is AsyncImagePainter.State.Error -> {
                Button(painter::restart) {
                    Text("Retry")
                }
            }
        }
    }
}