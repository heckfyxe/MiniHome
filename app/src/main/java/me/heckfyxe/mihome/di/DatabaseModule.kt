package me.heckfyxe.mihome.di

import android.content.Context
import me.heckfyxe.mihome.data.local.database.AppDatabase
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
@Configuration
class DatabaseModule {
    @Singleton
    fun database(context: Context) = AppDatabase.create(context)

    @Singleton
    fun accountDao(database: AppDatabase) = database.accountDao()
}