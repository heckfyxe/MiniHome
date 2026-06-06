package me.heckfyxe.mihome.data.local.database

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import me.heckfyxe.mihome.data.local.database.converters.StringListConverter
import me.heckfyxe.mihome.data.local.database.dao.AccountDao
import me.heckfyxe.mihome.data.local.database.dao.DeviceDao
import me.heckfyxe.mihome.data.local.database.dao.HomeDao
import me.heckfyxe.mihome.data.local.database.entities.Account
import me.heckfyxe.mihome.data.local.database.entities.Device
import me.heckfyxe.mihome.data.local.database.entities.Home
import me.heckfyxe.mihome.data.local.database.entities.Room as RoomEntity

@Database(entities = [Account::class, Home::class, RoomEntity::class, Device::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao

    abstract fun homeDao(): HomeDao

    abstract fun deviceDao(): DeviceDao

    companion object {
        fun create(context: Context, converter: StringListConverter) =
            Room.databaseBuilder(context, AppDatabase::class.java, "database")
                .fallbackToDestructiveMigration()
                .addTypeConverter(converter)
                .build()
    }
}