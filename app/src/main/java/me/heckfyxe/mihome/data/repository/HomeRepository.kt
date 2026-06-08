package me.heckfyxe.mihome.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import me.heckfyxe.mihome.data.local.database.dao.AccountDao
import me.heckfyxe.mihome.data.local.database.dao.DeviceDao
import me.heckfyxe.mihome.data.local.database.dao.HomeDao
import me.heckfyxe.mihome.data.local.database.entities.toEntity
import me.heckfyxe.mihome.data.local.database.relation.HomeWithRooms
import me.heckfyxe.mihome.data.local.database.relation.toEntities
import me.heckfyxe.mihome.data.remote.XiaomiApi
import me.heckfyxe.mihome.di.UserScope
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scope(UserScope::class)
@Scoped
class HomeRepository(
    private val xiaomiApi: XiaomiApi,
    private val accountDao: AccountDao,
    private val homeDao: HomeDao,
    private val deviceDao: DeviceDao,
) {
    fun getHomesWithRoomsAndDevicesFlow() = homeDao.getHomesWithRoomsAndDevicesFlow()

    fun getDevicesFlow() = deviceDao.getDevicesFlow()

    suspend fun getHomes(): List<HomeWithRooms> {
        val homesResponse = xiaomiApi.getHomes()
        val entities = homesResponse.result.toEntities()
        val homes = entities.map { it.home }
        val rooms = entities.flatMap { it.rooms }
        homeDao.upsert(homes, rooms)
        return homeDao.getHomesWithRooms()
    }

    suspend fun getDevices() = coroutineScope {
        val homes = homeDao.getHomes()
        val devices = homes.map { home ->
            async {
                val result = xiaomiApi.getDevices(home.ownerId, home.id).result
                val deviceToRoomMap = HashMap<String, Long>()
                result.home?.roomlist?.forEach { room -> room.dids?.forEach { deviceToRoomMap[it] = room.id } }
                result.devices
                    ?.map { device -> device.toEntity(deviceToRoomMap[device.id]!!.toString()) }
                    ?: emptyList()
            }
        }.awaitAll().flatten()
        deviceDao.upsert(devices)
        return@coroutineScope deviceDao.getDevices()
    }

    suspend fun logout() {
        accountDao.delete()
    }
}