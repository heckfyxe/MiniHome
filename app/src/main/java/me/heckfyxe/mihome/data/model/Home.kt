package me.heckfyxe.mihome.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Home(
    val id: String,
    val name: String,
    @SerialName("uid") val userId: Long,
    val roomlist: List<Room>,
)
