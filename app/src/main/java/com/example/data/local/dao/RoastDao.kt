package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.RoastEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoastDao {
    @Query("SELECT * FROM roasts WHERE postId = :postId AND parentRoastId IS NULL AND status = 'visible' ORDER BY isTopRoast DESC, reactionCount DESC, createdAt DESC")
    fun getTopRoastsForPost(postId: String): Flow<List<RoastEntity>>

    @Query("SELECT * FROM roasts WHERE postId = :postId AND parentRoastId IS NULL AND status = 'visible' ORDER BY createdAt DESC")
    fun getNewestRoastsForPost(postId: String): Flow<List<RoastEntity>>

    @Query("SELECT * FROM roasts WHERE postId = :postId AND parentRoastId IS NULL AND status = 'visible' ORDER BY createdAt ASC")
    fun getOldestRoastsForPost(postId: String): Flow<List<RoastEntity>>

    @Query("SELECT * FROM roasts WHERE parentRoastId = :parentRoastId AND status = 'visible' ORDER BY createdAt ASC")
    fun getComebacksForRoast(parentRoastId: String): Flow<List<RoastEntity>>

    @Query("SELECT * FROM roasts WHERE id = :id LIMIT 1")
    fun getRoastById(id: String): Flow<RoastEntity?>

    @Query("SELECT * FROM roasts WHERE id = :id LIMIT 1")
    suspend fun getRoastByIdOnce(id: String): RoastEntity?

    @Query("SELECT * FROM roasts WHERE authorId = :authorId AND status = 'visible' ORDER BY reactionCount DESC, createdAt DESC")
    fun getRoastsByAuthor(authorId: String): Flow<List<RoastEntity>>

    @Query("SELECT * FROM roasts WHERE postId = :postId")
    suspend fun getAllRoastsForPostOnce(postId: String): List<RoastEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoast(roast: RoastEntity)

    @Update
    suspend fun updateRoast(roast: RoastEntity)

    @Query("UPDATE roasts SET status = :status WHERE id = :id")
    suspend fun updateRoastStatus(id: String, status: String)

    @Query("UPDATE roasts SET isTopRoast = (CASE WHEN id = :topRoastId THEN 1 ELSE 0 END) WHERE postId = :postId")
    suspend fun setTopRoast(postId: String, topRoastId: String)

    @Query("UPDATE roasts SET isPinned = :isPinned WHERE id = :id")
    suspend fun setPinned(id: String, isPinned: Boolean)

    @Query("UPDATE roasts SET reactionCount = reactionCount + :delta WHERE id = :id")
    suspend fun adjustReactionCount(id: String, delta: Int)

    @Query("UPDATE roasts SET replyCount = replyCount + :delta WHERE id = :id")
    suspend fun adjustReplyCount(id: String, delta: Int)

    @Query("DELETE FROM roasts WHERE id = :id")
    suspend fun deleteRoast(id: String)
}
