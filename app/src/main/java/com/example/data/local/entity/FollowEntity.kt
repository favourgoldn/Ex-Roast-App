package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "follows",
    indices = [Index(value = ["followerId", "followingId"], unique = true)]
)
data class FollowEntity(
    @PrimaryKey val id: String,
    val followerId: String,
    val followingId: String,
    val status: String = "accepted", // pending, accepted
    val createdAt: Long = System.currentTimeMillis()
)
