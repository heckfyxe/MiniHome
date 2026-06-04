package me.heckfyxe.mihome.data.local.database.entities

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import me.heckfyxe.mihome.data.model.AccountData

@Entity
data class Account(
    @PrimaryKey val userId: Long,
    val ssecurity: String,
    val cUserId: String,
    val passToken: String,
    val location: String,
    val serviceToken: String,
)

fun AccountData.toEntity(serviceToken: String) =
    Account(userId, ssecurity, cUserId, passToken, location, serviceToken)