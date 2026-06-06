package me.heckfyxe.mihome.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceInfo(
    @SerialName("did")
    val id: String,

    @SerialName("uid")
    val userId: Long,

    val token: String,

    val name: String,

    @SerialName("localip")
    val localIp: String? = null,

    val model: String,

    @SerialName("spec_type")
    val spec: String,

    val isOnline: Boolean,

    @SerialName("remote_controllable")
    val isRemoteControllable: Boolean
)
