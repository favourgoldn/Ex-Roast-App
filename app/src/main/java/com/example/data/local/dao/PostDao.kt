package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM posts WHERE status = 'published' ORDER BY createdAt DESC")
    fun getAllPublishedPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE status = 'published' ORDER BY hotScore DESC, reactionCount DESC, roastCount DESC LIMIT 50")
    fun getHotPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :id LIMIT 1")
    fun getPostById(id: String): Flow<PostEntity?>

    @Query("SELECT * FROM posts WHERE id = :id LIMIT 1")
    suspend fun getPostByIdOnce(id: String): PostEntity?

    @Query("SELECT * FROM posts WHERE authorId = :authorId AND status != 'deleted' ORDER BY createdAt DESC")
    fun getPostsByAuthor(authorId: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE authorId = :authorId AND isAnonymous = 0 AND status = 'published' ORDER BY createdAt DESC")
    fun getPublicPostsByAuthor(authorId: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE status = 'published' AND category = :category ORDER BY createdAt DESC")
    fun getPostsByCategory(category: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE status = 'published' AND (LOWER(headline) LIKE '%' || LOWER(:query) || '%' OR LOWER(body) LIKE '%' || LOWER(:query) || '%') ORDER BY createdAt DESC")
    fun searchPosts(query: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE status = 'published' AND body LIKE '%' || :tag || '%' ORDER BY createdAt DESC")
    fun getPostsByTag(tag: String): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("UPDATE posts SET status = :status WHERE id = :id")
    suspend fun updatePostStatus(id: String, status: String)

    @Query("UPDATE posts SET roastCount = roastCount + :delta WHERE id = :id")
    suspend fun adjustRoastCount(id: String, delta: Int)

    @Query("UPDATE posts SET reactionCount = reactionCount + :delta WHERE id = :id")
    suspend fun adjustReactionCount(id: String, delta: Int)

    @Query("UPDATE posts SET saveCount = saveCount + :delta WHERE id = :id")
    suspend fun adjustSaveCount(id: String, delta: Int)

    @Query("UPDATE posts SET shareCount = shareCount + 1 WHERE id = :id")
    suspend fun incrementShareCount(id: String)

    @Query("UPDATE posts SET hotScore = :hotScore WHERE id = :id")
    suspend fun updateHotScore(id: String, hotScore: Double)

    @Query("UPDATE posts SET pinnedRoastId = :roastId WHERE id = :id")
    suspend fun setPinnedRoast(id: String, roastId: String?)

    @Query("UPDATE posts SET roastsMode = :mode WHERE id = :id")
    suspend fun setRoastsMode(id: String, mode: String)

    @Query("DELETE FROM posts WHERE id = :id")
    suspend fun deletePost(id: String)
}
