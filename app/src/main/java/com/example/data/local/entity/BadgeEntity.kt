package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "badges",
    indices = [Index(value = ["userId", "badgeId"], unique = true)]
)
data class BadgeEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val badgeId: String,
    val title: String,
    val description: String,
    val iconName: String,
    val awardedAt: Long = System.currentTimeMillis()
)
