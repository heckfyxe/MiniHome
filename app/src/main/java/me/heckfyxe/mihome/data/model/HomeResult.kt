package me.heckfyxe.mihome.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeResult(
    @SerialName("homelist")
    val homes: List<Home>,

    @SerialName("share_home_list")
    val sharedHomes: List<Home>,

    @SerialName("has_more")
    val hasMore: Boolean,

    @SerialName("max_id")
    val maxId: String,
)