package me.heckfyxe.mihome.data.local.database.converters

import androidx.room3.ProvidedTypeConverter
import androidx.room3.TypeConverter
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Singleton

@Singleton
@ProvidedTypeConverter
class StringListConverter(private val json: Json) {
    @TypeConverter
    fun convertToString(list: List<String>) = json.encodeToString(list)

    @TypeConverter
    fun convertFromString(str: String): List<String> = json.decodeFromString(str)
}