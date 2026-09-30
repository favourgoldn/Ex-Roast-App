package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "banned_terms")
data class BannedTermEntity(
    @PrimaryKey val id: String,
    val pattern: String,
    val isRegex: Boolean = false,
    val severity: String = "block", // block, flag
    val createdBy: String = "system",
    val createdAt: Long = System.currentTimeMillis()
)
