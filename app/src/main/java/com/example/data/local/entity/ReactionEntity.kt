package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reactions",
    indices = [Index(value = ["userId", "targetType", "targetId"], unique = true)]
)
data class ReactionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val targetType: String, // "post" or "roast"
    val targetId: String,
    val reactionType: String, // "flame", "skull", "cringe", "laugh", "red_flag"
    val createdAt: Long = System.currentTimeMillis()
)
