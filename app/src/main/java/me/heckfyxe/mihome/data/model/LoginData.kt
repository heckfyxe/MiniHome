package me.heckfyxe.mihome.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.heckfyxe.mihome.util.SecondsToDurationSerializer
import kotlin.time.Duration

@Serializable
data class LoginData(
    val loginUrl: String,
    val qr: String,
    @SerialName("lp") val pollingUrl: String,
    @Serializable(SecondsToDurationSerializer::class) val timeout: Duration,
)