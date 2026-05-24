package me.heckfyxe.mihome.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AccountData(
    val userId: Long,
    val ssecurity: String,
    val cUserId: String,
    val passToken: String,
    val location: String,
)