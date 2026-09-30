package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val authorId: String,
    val isAnonymous: Boolean = true,
    val anonAlias: String = "Anonymous Roaster",
    val headline: String,
    val body: String,
    val category: String, // First Dates, Situationships, Texting Crimes, Cheaters, Meet the Parents, Breakup Fails, Gifts Gone Wrong, Social Media Stalking, Red Flags, Redemption Arcs
    val intensity: String = "Medium", // Mild, Medium, Extra Crispy
    val contentWarning: String? = null,
    val imageUrl: String? = null,
    val roastsMode: String = "everyone", // everyone, followers, off
    val status: String = "published", // published, hidden_pending_review, removed, deleted
    val pinnedRoastId: String? = null,
    val editedAt: Long? = null,
    val deletedAt: Long? = null,
    val roastCount: Int = 0,
    val reactionCount: Int = 0,
    val saveCount: Int = 0,
    val shareCount: Int = 0,
    val viewCount: Int = 0,
    val hotScore: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)
