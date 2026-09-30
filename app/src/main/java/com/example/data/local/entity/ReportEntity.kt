package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey val id: String,
    val reporterId: String,
    val targetType: String, // "post", "roast", "user"
    val targetId: String,
    val targetSnippet: String = "",
    val reason: String, // Real person identified, Harassment, Hate speech, Sexual content, Threats, Self-harm, Spam, Misinformation, Underage, Other
    val note: String = "",
    val status: String = "open", // open, actioned, dismissed, escalated
    val priority: String = "medium", // high, medium, low
    val resolvedBy: String? = null,
    val resolvedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
