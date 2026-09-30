package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.BlockMuteEntity
import com.example.data.local.entity.FollowEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.SaveEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SocialDao {
    // Follows
    @Query("SELECT followingId FROM follows WHERE followerId = :userId AND status = 'accepted'")
    fun getFollowingIds(userId: String): Flow<List<String>>

    @Query("SELECT followerId FROM follows WHERE followingId = :userId AND status = 'accepted'")
    fun getFollowerIds(userId: String): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM follows WHERE followingId = :userId AND status = 'accepted'")
    fun getFollowerCount(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM follows WHERE followerId = :userId AND status = 'accepted'")
    fun getFollowingCount(userId: String): Flow<Int>

    @Query("SELECT * FROM follows WHERE followerId = :followerId AND followingId = :followingId LIMIT 1")
    fun getFollow(followerId: String, followingId: String): Flow<FollowEntity?>

    @Query("SELECT * FROM follows WHERE followingId = :userId AND status = 'pending'")
    fun getPendingFollowRequests(userId: String): Flow<List<FollowEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollow(follow: FollowEntity)

    @Query("DELETE FROM follows WHERE followerId = :followerId AND followingId = :followingId")
    suspend fun deleteFollow(followerId: String, followingId: String)

    @Query("UPDATE follows SET status = 'accepted' WHERE id = :id")
    suspend fun acceptFollowRequest(id: String)

    // Blocks & Mutes
    @Query("SELECT targetUserId FROM blocks_mutes WHERE userId = :userId AND isBlock = 1")
    fun getBlockedUserIds(userId: String): Flow<List<String>>

    @Query("SELECT targetUserId FROM blocks_mutes WHERE userId = :userId AND isBlock = 0")
    fun getMutedUserIds(userId: String): Flow<List<String>>

    @Query("SELECT * FROM blocks_mutes WHERE userId = :userId AND isBlock = 1")
    fun getBlockedUsers(userId: String): Flow<List<BlockMuteEntity>>

    @Query("SELECT * FROM blocks_mutes WHERE userId = :userId AND isBlock = 0")
    fun getMutedUsers(userId: String): Flow<List<BlockMuteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM blocks_mutes WHERE userId = :userId AND targetUserId = :targetId AND isBlock = 1)")
    suspend fun isUserBlocked(userId: String, targetId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockMute(entity: BlockMuteEntity)

    @Query("DELETE FROM blocks_mutes WHERE userId = :userId AND targetUserId = :targetUserId AND isBlock = :isBlock")
    suspend fun deleteBlockMute(userId: String, targetUserId: String, isBlock: Boolean)

    // Saves
    @Query("SELECT postId FROM saves WHERE userId = :userId")
    fun getSavedPostIds(userId: String): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM saves WHERE userId = :userId AND postId = :postId)")
    fun isPostSaved(userId: String, postId: String): Flow<Boolean>

    @Query("SELECT DISTINCT collectionName FROM saves WHERE userId = :userId")
    fun getCollections(userId: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSave(save: SaveEntity)

    @Query("DELETE FROM saves WHERE userId = :userId AND postId = :postId")
    suspend fun deleteSave(userId: String, postId: String)

    // Notifications
    @Query("SELECT * FROM notifications WHERE recipientId = :recipientId ORDER BY createdAt DESC")
    fun getNotifications(recipientId: String): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE recipientId = :recipientId AND isRead = 0")
    fun getUnreadNotificationCount(recipientId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE recipientId = :recipientId")
    suspend fun markAllNotificationsAsRead(recipientId: String)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: String)
}
