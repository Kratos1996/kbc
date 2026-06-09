package com.ishan.kbc.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun listToString(value: List<String>?): String = value?.joinToString("|") ?: ""

    @TypeConverter
    fun stringToList(value: String?): List<String> =
        value?.takeIf { it.isNotBlank() }?.split("|")?.filter { it.isNotBlank() } ?: emptyList()
}
