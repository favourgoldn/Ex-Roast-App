package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class UserProfileEntity(
    @PrimaryKey val userId: String,
    val username: String,
    val displayName: String,
    val bio: String = "",
    val avatarUrl: String = "",
    val roastStyle: String = "Sarcastic", // Savage, Sarcastic, Wholesome Burn, Dry Wit
    val interests: String = "First Dates,Texting Crimes,Red Flags", // Comma-delimited
    val role: String = "member", // member, moderator, admin
    val isPrivate: Boolean = false,
    val dateOfBirth: String = "",
    val ageVerified: Boolean = true,
    val termsAccepted: Boolean = true,
    val roastScore: Int = 0,
    val level: String = "Rookie", // Rookie, Sizzler, Flame Thrower, Inferno, Roast Legend
    val currentStreak: Int = 1,
    val longestStreak: Int = 1,
    val lastActiveDate: Long = System.currentTimeMillis(),
    val streakFreezes: Int = 0,
    val status: String = "active", // active, suspended, banned
    val deletionRequestedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
