package me.heckfyxe.mihome.data.local.database.entities

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity
data class Home(
    @PrimaryKey val id: String,
    val name: String,
    val ownerId: String,
)