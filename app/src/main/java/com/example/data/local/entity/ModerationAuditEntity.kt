package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moderation_audits")
data class ModerationAuditEntity(
    @PrimaryKey val id: String,
    val moderatorId: String,
    val moderatorName: String,
    val action: String, // dismiss, remove_content, warn_user, temp_ban_1, temp_ban_7, temp_ban_30, ban_perm, escalate
    val targetType: String,
    val targetId: String,
    val reason: String,
    val metadata: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
