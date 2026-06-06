package me.heckfyxe.mihome.data.local.database.entities

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.TypeConverters
import me.heckfyxe.mihome.data.local.database.converters.StringListConverter

@Entity
data class Room(
    @PrimaryKey val id: String,
    val homeId: String,
    val name: String,

    @field:TypeConverters(StringListConverter::class)
    val dids: List<String>,
)