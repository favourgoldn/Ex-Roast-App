package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "saves",
    indices = [Index(value = ["userId", "postId"], unique = true)]
)
data class SaveEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val postId: String,
    val collectionName: String = "Saved",
    val createdAt: Long = System.currentTimeMillis()
)
