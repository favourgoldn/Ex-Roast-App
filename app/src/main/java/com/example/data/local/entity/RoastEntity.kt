package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roasts")
data class RoastEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val authorId: String,
    val authorName: String,
    val authorAvatar: String,
    val parentRoastId: String? = null, // null for top-level roast, set for comebacks
    val body: String,
    val status: String = "visible", // visible, hidden_by_author, removed, deleted
    val isTopRoast: Boolean = false,
    val isPinned: Boolean = false,
    val reactionCount: Int = 0,
    val replyCount: Int = 0,
    val clientRequestId: String = "",
    val editedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
