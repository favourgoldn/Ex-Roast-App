package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM profiles WHERE userId = :userId LIMIT 1")
    fun getProfileById(userId: String): Flow<UserProfileEntity?>

    @Query("SELECT * FROM profiles WHERE userId = :userId LIMIT 1")
    suspend fun getProfileByIdOnce(userId: String): UserProfileEntity?

    @Query("SELECT * FROM profiles WHERE LOWER(username) = LOWER(:username) LIMIT 1")
    suspend fun getProfileByUsername(username: String): UserProfileEntity?

    @Query("SELECT * FROM profiles WHERE status = 'active' ORDER BY roastScore DESC LIMIT 50")
    fun getTopRoasters(): Flow<List<UserProfileEntity>>

    @Query("SELECT * FROM profiles WHERE LOWER(username) LIKE '%' || LOWER(:query) || '%' OR LOWER(displayName) LIKE '%' || LOWER(:query) || '%'")
    fun searchUsers(query: String): Flow<List<UserProfileEntity>>

    @Query("SELECT * FROM profiles")
    fun getAllProfiles(): Flow<List<UserProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfileEntity)

    @Update
    suspend fun update(profile: UserProfileEntity)

    @Query("UPDATE profiles SET role = :newRole WHERE userId = :userId")
    suspend fun updateUserRole(userId: String, newRole: String)

    @Query("UPDATE profiles SET status = :status WHERE userId = :userId")
    suspend fun updateUserStatus(userId: String, status: String)

    @Query("UPDATE profiles SET roastScore = roastScore + :delta WHERE userId = :userId")
    suspend fun incrementRoastScore(userId: String, delta: Int)

    @Query("DELETE FROM profiles WHERE userId = :userId")
    suspend fun deleteUser(userId: String)
}
