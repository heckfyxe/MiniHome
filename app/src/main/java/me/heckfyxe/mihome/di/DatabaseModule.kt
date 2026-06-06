package me.heckfyxe.mihome.di

import android.content.Context
import me.heckfyxe.mihome.data.local.database.AppDatabase
import me.heckfyxe.mihome.data.local.database.converters.StringListConverter
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
@Configuration
class DatabaseModule {
    @Singleton
    fun database(context: Context, converter: StringListConverter) = AppDatabase.create(context, converter)

    @Singleton
    fun accountDao(database: AppDatabase) = database.accountDao()

    @Singleton
    fun homeDao(database: AppDatabase) = database.homeDao()

    @Singleton
    fun deviceDao(database: AppDatabase) = database.deviceDao()
}