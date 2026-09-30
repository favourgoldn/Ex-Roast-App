package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ReactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReactionDao {
    @Query("SELECT * FROM reactions WHERE targetType = :targetType AND targetId = :targetId")
    fun getReactionsForTarget(targetType: String, targetId: String): Flow<List<ReactionEntity>>

    @Query("SELECT * FROM reactions WHERE targetType = :targetType AND targetId = :targetId")
    suspend fun getReactionsForTargetOnce(targetType: String, targetId: String): List<ReactionEntity>

    @Query("SELECT * FROM reactions WHERE userId = :userId AND targetType = :targetType AND targetId = :targetId LIMIT 1")
    fun getUserReaction(userId: String, targetType: String, targetId: String): Flow<ReactionEntity?>

    @Query("SELECT * FROM reactions WHERE userId = :userId AND targetType = :targetType AND targetId = :targetId LIMIT 1")
    suspend fun getUserReactionOnce(userId: String, targetType: String, targetId: String): ReactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReaction(reaction: ReactionEntity)

    @Query("DELETE FROM reactions WHERE userId = :userId AND targetType = :targetType AND targetId = :targetId")
    suspend fun deleteReaction(userId: String, targetType: String, targetId: String)
}
