package me.heckfyxe.mihome.data.repository

import me.heckfyxe.mihome.data.local.database.dao.DeviceDao
import me.heckfyxe.mihome.data.remote.Miot
import me.heckfyxe.mihome.di.DeviceScope
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scope(DeviceScope::class)
@Scoped
class DeviceRepository(private val miot: Miot, private val deviceDao: DeviceDao) {
    fun getDeviceFlow(deviceId: String) = deviceDao.getDeviceFlow(deviceId)

    suspend fun <T : Any> setProperty(serviceId: Int, propertyId: Int, value: T) = miot.send(
        "set_properties",
        listOf(mapOf("siid" to serviceId, "piid" to propertyId, "value" to value))
    )
}