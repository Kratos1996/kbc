package com.ishan.kbc.domain.model

data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val isEarned: Boolean,
    val earnedDate: String? = null,
    val rarity: String? = null,
)

data class Milestone(
    val name: String,
    val description: String,
    val tier: String,
    val tierColor: String,
    val reward: String,
    val timeAgo: String,
)
