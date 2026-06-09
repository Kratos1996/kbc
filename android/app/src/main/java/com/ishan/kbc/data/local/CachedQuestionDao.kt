package com.ishan.kbc.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CachedQuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(question: CachedQuestion)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<CachedQuestion>)

    @Update
    suspend fun update(question: CachedQuestion)

    @Delete
    suspend fun delete(question: CachedQuestion)

    @Query("SELECT * FROM cached_questions WHERE id = :id")
    suspend fun getById(id: String): CachedQuestion?

    @Query("SELECT * FROM cached_questions WHERE categoryId = :categoryId")
    suspend fun getByCategory(categoryId: String): List<CachedQuestion>

    @Query("SELECT * FROM cached_questions")
    suspend fun getAll(): List<CachedQuestion>

    @Query("DELETE FROM cached_questions")
    suspend fun deleteAll()

    @Query("DELETE FROM cached_questions WHERE cachedAt < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)
}
