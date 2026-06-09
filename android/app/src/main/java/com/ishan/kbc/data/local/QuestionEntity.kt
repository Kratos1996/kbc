package com.ishan.kbc.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey
    val id: String,
    val text: String,
    val options: List<String>,
    val difficulty: Int,
    val categoryId: String,
    val categoryName: String,
    val correctOption: Int? = null,
    val explanation: String? = null,
)
