package com.ishan.kbc.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_categories")
data class CachedCategory(
    @PrimaryKey val id: String,
    val name: String,
    val slug: String,
    val icon: String? = null,
    val sortOrder: Int = 0,
    val cachedAt: Long = System.currentTimeMillis(),
)
