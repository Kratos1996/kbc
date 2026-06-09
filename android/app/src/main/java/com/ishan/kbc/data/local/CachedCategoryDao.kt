package com.ishan.kbc.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CachedCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CachedCategory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CachedCategory>)

    @Update
    suspend fun update(category: CachedCategory)

    @Delete
    suspend fun delete(category: CachedCategory)

    @Query("SELECT * FROM cached_categories WHERE id = :id")
    suspend fun getById(id: String): CachedCategory?

    @Query("SELECT * FROM cached_categories ORDER BY sortOrder ASC")
    suspend fun getAll(): List<CachedCategory>

    @Query("DELETE FROM cached_categories")
    suspend fun deleteAll()

    @Query("DELETE FROM cached_categories WHERE cachedAt < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)
}
