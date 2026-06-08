package me.heckfyxe.mihome.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow
import me.heckfyxe.mihome.data.local.database.entities.Device

@Dao
interface DeviceDao {
    @Upsert
    suspend fun upsert(devices: List<Device>)

    @Query("SELECT * FROM Device")
    suspend fun getDevices(): List<Device>

    @Query("SELECT * FROM Device")
    fun getDevicesFlow(): Flow<List<Device>>

    @Query("SELECT * FROM Device WHERE id = :deviceId")
    fun getDeviceFlow(deviceId: String): Flow<Device>
}