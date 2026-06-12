package me.heckfyxe.mihome.domain.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrColors
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrLogo
import io.github.alexzhirkevich.qrose.options.QrLogoPadding
import io.github.alexzhirkevich.qrose.options.QrLogoShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.QrShapes
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import me.heckfyxe.mihome.R
import me.heckfyxe.mihome.data.model.LoginData
import me.heckfyxe.mihome.data.model.XiaomiCountry
import me.heckfyxe.mihome.ui.theme.MiHomeTheme
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.seconds

@Composable
fun LoginScreen(viewModel: LoginViewModel = koinViewModel()) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    NewLoginScreenContent(state, viewModel::loadAgain, { viewModel.openInBrowser(context) })
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun NewLoginScreenContent(
    state: LoginUiState,
    tryAgain: () -> Unit,
    openInBrowser: () -> Unit,
    showQrFirst: Boolean = true,
) {
    Scaffold(Modifier.fillMaxSize()) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(vertical = 32.dp, horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    modifier = Modifier.size(72.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(22.dp),
                ) {
                    Icon(
                        modifier = Modifier
                            .wrapContentSize()
                            .size(48.dp),
                        imageVector = Icons.Filled.Home,
                        contentDescription = null,
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(stringResource(R.string.welcome_to_smart_home), style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(R.string.sign_in_to_control_your_devices),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(24.dp))

                val resources = LocalResources.current
                var showQrCode by rememberSaveable { mutableStateOf(showQrFirst) }
                ButtonGroup(modifier = Modifier.fillMaxWidth(), overflowIndicator = {}) {
                    toggleableItem(
                        weight = 1f,
                        checked = showQrCode,
                        label = resources.getString(R.string.qr_code),
                        onCheckedChange = { showQrCode = !showQrCode },
                        icon = { Icon(Icons.Default.QrCode, contentDescription = null) },
                    )
                    toggleableItem(
                        weight = 1f,
                        checked = !showQrCode,
                        label = resources.getString(R.string.browser),
                        onCheckedChange = { showQrCode = !showQrCode },
                        icon = { Icon(Icons.Default.Language, contentDescription = null) }
                    )
                }

                Spacer(Modifier.height(16.dp))

                if (showQrCode) {
                    QrLoginContent(state = state, tryAgain = tryAgain)
                } else {
                    BrowserLoginContent(state = state, openInBrowser = openInBrowser)
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                var isExpanded by remember { mutableStateOf(false) }
                var region by remember { mutableStateOf(XiaomiCountry.Germany) }
                ExposedDropdownMenuBox(isExpanded, onExpandedChange = { isExpanded = it }) {
                    Row(
                        Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Public, contentDescription = null)
                        Text(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            text = stringResource(R.string.region, region.name),
                            maxLines = 1,
                            softWrap = false
                        )
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                    }
                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false },
                        containerColor = MenuDefaults.groupStandardContainerColor,
                        shape = MenuDefaults.standaloneGroupShape,
                    ) {
                        XiaomiCountry.entries.forEachIndexed { index, country ->
                            DropdownMenuItem(
                                selected = region == country,
                                shapes = MenuDefaults.itemShape(index, XiaomiCountry.entries.size),
                                text = { Text(country.name, style = MaterialTheme.typography.bodyLarge) },
                                onClick = { region = country }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    stringResource(R.string.agree_terms_and_privacy_policy),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun QrLoginContent(modifier: Modifier = Modifier, state: LoginUiState, tryAgain: () -> Unit) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF4FBF5),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 32.dp, horizontal = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (state) {
                LoginUiState.Loading -> LoadingIndicator()
                LoginUiState.LoadingError -> TextButton(onClick = tryAgain) { Text("Try again") }
                else -> {}
            }

            AnimatedVisibility(
                state is LoginUiState.Loaded || state is LoginUiState.Refreshing || state is LoginUiState.RefreshError,
                enter = expandIn(
                    expandFrom = Alignment.TopCenter,
                    initialSize = { IntSize(it.width, 0) },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioHighBouncy,
                        stiffness = Spring.StiffnessMediumLow,
                    )
                )
            ) {
                QrCode(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxWidth()
                        .alpha(if (state is LoginUiState.Loaded) 1f else 0.2f),
                    data = when (state) {
                        is LoginUiState.Loaded -> state.data.loginUrl
                        is LoginUiState.RefreshError -> state.oldData.loginUrl
                        is LoginUiState.Refreshing -> state.oldData.loginUrl
                        else -> ""
                    },
                )
            }

            Spacer(Modifier.height(24.dp))

            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.surface) {
                Text(stringResource(R.string.scan_to_sign_in), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(R.string.how_to_scan_qr_code),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    Spacer(Modifier.height(8.dp))

    when (state) {
        is LoginUiState.Loaded, is LoginUiState.Refreshing -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LoadingIndicator()
                Text(stringResource(if (state is LoginUiState.Loaded) R.string.waiting_for_scan else R.string.qr_expired_refreshing))
            }
        }

        is LoginUiState.RefreshError -> {
            TextButton(onClick = tryAgain) {
                Text(stringResource(R.string.try_again))
            }
        }

        else -> {}
    }
}

@Composable
private fun QrCode(modifier: Modifier = Modifier, data: String, alpha: Float = 1f, iconAlpha: Float = 1f) {
    val qrBrush = remember { QrBrush.solid(Color(0x0E / 255f, 0x2A / 255f, 0x1A / 255f, alpha)) }
    Image(
        modifier = modifier,
        painter = rememberQrCodePainter(
            data = data,
            colors = QrColors(
                dark = qrBrush,
                frame = qrBrush,
                ball = qrBrush,
            ),
            shapes = QrShapes(
                darkPixel = QrPixelShape.circle(),
                frame = QrFrameShape.roundCorners(0.2f),
                ball = QrBallShape.roundCorners(0.2f)
            ),
            logo = QrLogo(
                painter = rememberAirIconPainter(iconAlpha),
                shape = QrLogoShape.roundCorners(0.25f),
                padding = QrLogoPadding.Natural(0f),
            ),
        ),
        contentDescription = stringResource(R.string.qr_code),
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BrowserLoginContent(modifier: Modifier = Modifier, state: LoginUiState, openInBrowser: () -> Unit) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Card(Modifier.fillMaxWidth()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.OpenInBrowser,
                    modifier = Modifier.size(52.dp),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.sign_in_with_your_account), style = MaterialTheme.typography.titleSmall, color = Color.White)
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(R.string.you_will_be_redirected),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        val size = ButtonDefaults.MediumContainerHeight
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(size),
            enabled = state is LoginUiState.Loaded,
            onClick = openInBrowser,
            contentPadding = ButtonDefaults.contentPaddingFor(size, hasStartIcon = true),
            shapes = ButtonDefaults.shapesFor(size),
        ) {
            Icon(
                modifier = Modifier.size(ButtonDefaults.iconSizeFor(size)),
                imageVector = Icons.AutoMirrored.Default.OpenInNew,
                tint = MaterialTheme.colorScheme.onPrimary,
                contentDescription = null
            )
            Spacer(Modifier.width(ButtonDefaults.iconSpacingFor(size)))
            Text(stringResource(R.string.continue_in_browser), style = ButtonDefaults.textStyleFor(size))
        }
        Spacer(Modifier.height(16.dp))
        Row {
            Icon(Icons.Outlined.VerifiedUser, tint = MaterialTheme.colorScheme.primary, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.we_never_see_you_password), style = MaterialTheme.typography.bodySmall)
        }
    }
}

private val sampleData = LoginData("https://github.com/heckfyxe/minihome", "", 0.seconds)

@Preview
@Composable
private fun LoginScreenQrPreview() {
    MiHomeTheme {
        NewLoginScreenContent(LoginUiState.Loaded(sampleData), {}, {})
    }
}

@Preview
@Composable
private fun LoginScreenQrLoadingPreview() {
    MiHomeTheme {
        NewLoginScreenContent(LoginUiState.Loading, {}, {})
    }
}

@Preview
@Composable
private fun LoginScreenQrLoadingErrorPreview() {
    MiHomeTheme {
        NewLoginScreenContent(LoginUiState.LoadingError, {}, {})
    }
}

@Preview
@Composable
private fun LoginScreenQrRefreshingPreview() {
    MiHomeTheme {
        NewLoginScreenContent(LoginUiState.Refreshing(sampleData), {}, {})
    }
}

@Preview
@Composable
private fun LoginScreenQrRefreshErrorPreview() {
    MiHomeTheme {
        NewLoginScreenContent(LoginUiState.RefreshError(sampleData), {}, {})
    }
}

@Preview
@Composable
private fun LoginScreenBrowserPreview() {
    MiHomeTheme {
        NewLoginScreenContent(
            state = LoginUiState.Loaded(sampleData),
            tryAgain = {},
            openInBrowser = {},
            showQrFirst = false
        )
    }
}
