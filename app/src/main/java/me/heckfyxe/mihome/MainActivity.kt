package me.heckfyxe.mihome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import me.heckfyxe.mihome.domain.auth.LoginScreen
import me.heckfyxe.mihome.ui.theme.MiHomeTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiHomeTheme {
                LoginScreen()
            }
        }
    }
}