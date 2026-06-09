package com.ishan.kbc.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [CachedQuestion::class, CachedCategory::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class KbcDatabase : RoomDatabase() {
    abstract fun cachedQuestionDao(): CachedQuestionDao
    abstract fun cachedCategoryDao(): CachedCategoryDao

    companion object {
        const val DB_NAME = "kbc.db"
    }
}
