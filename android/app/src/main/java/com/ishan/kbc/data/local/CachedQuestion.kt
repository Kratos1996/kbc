package com.ishan.kbc.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_questions")
data class CachedQuestion(
    @PrimaryKey val id: String,
    val text: String,
    val options: String, // pipe-separated
    val difficulty: Int,
    val categoryId: String,
    val categoryName: String,
    val correctOption: Int? = null,
    val explanation: String? = null,
    val cachedAt: Long = System.currentTimeMillis(),
)
