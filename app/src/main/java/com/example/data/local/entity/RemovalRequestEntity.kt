package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "removal_requests")
data class RemovalRequestEntity(
    @PrimaryKey val id: String,
    val requesterEmail: String,
    val postId: String,
    val explanation: String,
    val status: String = "pending", // pending, approved, rejected
    val resolvedBy: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
