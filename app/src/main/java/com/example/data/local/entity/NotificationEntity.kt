package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val recipientId: String,
    val type: String, // "roast", "comeback", "reaction", "top_roast", "follower", "badge", "moderation"
    val actorId: String? = null,
    val actorName: String = "",
    val actorAvatar: String = "",
    val postId: String? = null,
    val roastId: String? = null,
    val message: String,
    val groupKey: String = "",
    val count: Int = 1,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
