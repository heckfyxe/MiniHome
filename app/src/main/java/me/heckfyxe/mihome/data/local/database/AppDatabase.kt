package me.heckfyxe.mihome.data.local.database

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import me.heckfyxe.mihome.data.local.database.dao.AccountDao
import me.heckfyxe.mihome.data.local.database.entities.Account

@Database(entities = [Account::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao

    companion object {
        fun create(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, "database").build()
    }
}