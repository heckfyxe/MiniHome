package me.heckfyxe.mihome.data.local.database.entities

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import me.heckfyxe.mihome.data.model.DeviceInfo

@Entity
data class Device(
    @PrimaryKey val id: String,
    val userId: Long,
    val roomId: String,
    val token: String,
    val name: String,
    val localIp: String?,
    val model: String,
    val spec: String,
    val isOnline: Boolean,
    val isRemoteControllable: Boolean,
)

fun DeviceInfo.toEntity(roomId: String) = Device(
    id = id,
    userId = userId,
    roomId = roomId,
    token = token,
    name = name,
    localIp = localIp,
    model = model,
    spec = spec,
    isOnline = isOnline,
    isRemoteControllable = isRemoteControllable
)