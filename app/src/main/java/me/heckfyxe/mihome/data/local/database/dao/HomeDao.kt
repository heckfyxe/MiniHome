package me.heckfyxe.mihome.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow
import me.heckfyxe.mihome.data.local.database.entities.Home
import me.heckfyxe.mihome.data.local.database.entities.Room
import me.heckfyxe.mihome.data.local.database.relation.HomeWithRooms
import me.heckfyxe.mihome.data.local.database.relation.HomeWithRoomsAndDevices

@Dao
interface HomeDao {
    @Upsert
    suspend fun upsert(homes: List<Home>, rooms: List<Room>)

    @Query("SELECT * FROM Home")
    suspend fun getHomes(): List<Home>

    @Transaction
    @Query("SELECT * FROM Home")
    suspend fun getHomesWithRooms(): List<HomeWithRooms>

    @Transaction
    @Query("SELECT * FROM Home")
    suspend fun getHomesWithRoomsAndDevices(): List<HomeWithRoomsAndDevices>

    @Transaction
    @Query("SELECT * FROM Home")
    fun getHomesWithRoomsAndDevicesFlow(): Flow<List<HomeWithRoomsAndDevices>>
}