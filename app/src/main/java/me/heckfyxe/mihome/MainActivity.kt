package me.heckfyxe.mihome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable
import me.heckfyxe.mihome.data.local.database.entities.Device
import me.heckfyxe.mihome.di.DeviceScope
import me.heckfyxe.mihome.di.UserScope
import me.heckfyxe.mihome.domain.auth.LoginScreen
import me.heckfyxe.mihome.domain.device.DeviceScreen
import me.heckfyxe.mihome.domain.home.HomeScreen
import me.heckfyxe.mihome.ui.theme.MiHomeTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.compose.getKoin
import org.koin.compose.scope.KoinScope
import org.koin.compose.scope.UnboundKoinScope
import org.koin.core.annotation.KoinDelicateAPI
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.component.getScopeId

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModel()

    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splash.setKeepOnScreenCondition { viewModel.state.value is MainActivityUiState.Loading }

        setContent {
            MiHomeTheme {
                val state by viewModel.state.collectAsStateWithLifecycle()
                val navBackStack = rememberNavBackStack(Splash)

                NavDisplay(
                    backStack = navBackStack,
                    onBack = { navBackStack.removeLastOrNull() },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator(),
                        rememberUserScopeNavEntryDecorator(),
                    ),
                    entryProvider = entryProvider {
                        entry(Splash) { }
                        entry(LoginScreen) { LoginScreen() }
                        entry(HomeScreen) { HomeScreen(onDeviceClick = { navBackStack.add(DeviceScreen(it)) }) }
                        entry<DeviceScreen> { screen ->
                            KoinScope({
                                getOrCreateScope<DeviceScope>(DeviceScreen.getScopeId())
                                    .apply { declare(screen.device) }
                            }) {
                                DeviceScreen()
                            }
                        }
                    },
                )

                LaunchedEffect(state) {
                    if (state is MainActivityUiState.Loading) return@LaunchedEffect

                    navBackStack.clear()
                    navBackStack.add(if (state is MainActivityUiState.LoggedIn) HomeScreen else LoginScreen)
                }
            }
        }
    }
}

@Composable
fun <T : Any> rememberUserScopeNavEntryDecorator(): NavEntryDecorator<T> {
    return remember { UserScopeNavEntryDecorator() }
}

@OptIn(KoinDelicateAPI::class, KoinExperimentalAPI::class)
class UserScopeNavEntryDecorator<T : Any> : NavEntryDecorator<T>(decorate = { entry ->
    val scope = getKoin().getScopeOrNull(UserScope.getScopeId())
    if (scope != null) {
        UnboundKoinScope(scope) {
            entry.Content()
        }
    } else {
        entry.Content()
    }
})

@Serializable
data object Splash : NavKey

@Serializable
data object LoginScreen : NavKey

@Serializable
data object HomeScreen : NavKey

@Immutable
@Serializable
data class DeviceScreen(val device: Device) : NavKey