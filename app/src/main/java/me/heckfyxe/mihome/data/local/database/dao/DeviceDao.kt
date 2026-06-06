package me.heckfyxe.mihome.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import me.heckfyxe.mihome.data.local.database.entities.Device

@Dao
interface DeviceDao {
    @Upsert
    suspend fun upsert(devices: List<Device>)

    @Query("SELECT * FROM Device")
    suspend fun getDevices(): List<Device>
}