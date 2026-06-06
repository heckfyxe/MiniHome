package me.heckfyxe.mihome.data.local.database.relation

import androidx.room3.Embedded
import androidx.room3.Relation
import me.heckfyxe.mihome.data.local.database.entities.Home
import me.heckfyxe.mihome.data.local.database.entities.Room

data class HomeWithRoomsAndDevices(
    @Embedded val home: Home,

    @Relation(Room::class, parentColumns = ["id"], entityColumns = ["homeId"])
    val rooms: List<RoomWithDevices>
)