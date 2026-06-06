package me.heckfyxe.mihome.data.local.database.relation

import androidx.room3.Embedded
import androidx.room3.Relation
import me.heckfyxe.mihome.data.local.database.entities.Home
import me.heckfyxe.mihome.data.local.database.entities.Room
import me.heckfyxe.mihome.data.model.HomeResult
import me.heckfyxe.mihome.data.model.Home as HomeModel

data class HomeWithRooms(
    @Embedded val home: Home,

    @Relation(parentColumns = ["id"], entityColumns = ["homeId"])
    val rooms: List<Room>,
)

fun HomeModel.toEntity() = HomeWithRooms(
    home = Home(id = id, name = name, ownerId = userId.toString()),
    rooms = roomlist.map { room ->
        Room(
            id = room.id,
            homeId = id,
            name = room.name,
            dids = room.dids
        )
    }
)

fun HomeResult.toEntities() = homes.map(HomeModel::toEntity) + sharedHomes.map(HomeModel::toEntity)

