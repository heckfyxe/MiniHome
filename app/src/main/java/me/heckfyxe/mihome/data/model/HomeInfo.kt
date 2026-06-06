package me.heckfyxe.mihome.data.model

import kotlinx.serialization.Serializable

@Serializable
data class HomeInfo(
    val id: Long,
    val dids: List<String>?,
    val roomlist: List<RoomWithDids>,
)
