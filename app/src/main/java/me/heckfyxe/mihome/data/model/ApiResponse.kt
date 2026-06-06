package me.heckfyxe.mihome.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val result: T,
)