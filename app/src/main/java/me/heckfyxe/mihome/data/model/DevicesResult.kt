package me.heckfyxe.mihome.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DevicesResult(
    @SerialName("home_info") val home: HomeInfo?,
    @SerialName("device_info") val devices: List<DeviceInfo>?,
)