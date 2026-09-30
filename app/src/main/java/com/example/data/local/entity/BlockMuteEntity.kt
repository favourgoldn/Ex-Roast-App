package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "blocks_mutes",
    indices = [Index(value = ["userId", "targetUserId", "isBlock"], unique = true)]
)
data class BlockMuteEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val targetUserId: String,
    val targetUsername: String = "",
    val isBlock: Boolean, // true = block, false = mute
    val createdAt: Long = System.currentTimeMillis()
)
