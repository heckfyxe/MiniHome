package me.heckfyxe.mihome.data.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.heckfyxe.mihome.util.SecondsToDurationSerializer
import kotlin.time.Duration

@Immutable
@Serializable
data class LoginData(
    val loginUrl: String,
    @SerialName("lp") val pollingUrl: String,
    @Serializable(SecondsToDurationSerializer::class) val timeout: Duration,
)