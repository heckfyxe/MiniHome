package me.heckfyxe.mihome.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Room(
    val id: String,
    val name: String,
    val dids: List<String>,
)
