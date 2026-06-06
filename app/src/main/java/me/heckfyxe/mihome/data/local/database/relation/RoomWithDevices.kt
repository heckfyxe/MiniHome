package me.heckfyxe.mihome.data.local.database.relation

import androidx.room3.Embedded
import androidx.room3.Relation
import me.heckfyxe.mihome.data.local.database.entities.Device
import me.heckfyxe.mihome.data.local.database.entities.Room

data class RoomWithDevices(
    @Embedded val room: Room,

    @Relation(parentColumns = ["id"], entityColumns = ["roomId"])
    val devices: List<Device>,
)