package com.ishan.kbc.domain.model

data class MatchEntry(
    val id: String,
    val date: String,
    val category: String,
    val prize: Int,
    val isWin: Boolean,
    val isHighlight: Boolean = false,
    val description: String? = null,
    val badge: String? = null,
)
