package me.heckfyxe.mihome.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RoomWithDids(val id: Long, val dids: List<String>?)