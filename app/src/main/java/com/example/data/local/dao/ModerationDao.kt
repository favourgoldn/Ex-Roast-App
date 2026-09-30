package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.BadgeEntity
import com.example.data.local.entity.BannedTermEntity
import com.example.data.local.entity.ModerationAuditEntity
import com.example.data.local.entity.RemovalRequestEntity
import com.example.data.local.entity.ReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModerationDao {
    // Reports
    @Query("SELECT * FROM reports ORDER BY CASE priority WHEN 'high' THEN 1 WHEN 'medium' THEN 2 ELSE 3 END, createdAt DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE status = 'open' ORDER BY CASE priority WHEN 'high' THEN 1 WHEN 'medium' THEN 2 ELSE 3 END, createdAt DESC")
    fun getOpenReports(): Flow<List<ReportEntity>>

    @Query("SELECT COUNT(*) FROM reports WHERE status = 'open'")
    fun getOpenReportCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Query("UPDATE reports SET status = :status, resolvedBy = :moderatorId, resolvedAt = :resolvedAt WHERE id = :reportId")
    suspend fun resolveReport(reportId: String, status: String, moderatorId: String, resolvedAt: Long)

    // Removal requests ("This is about me")
    @Query("SELECT * FROM removal_requests ORDER BY createdAt DESC")
    fun getAllRemovalRequests(): Flow<List<RemovalRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemovalRequest(request: RemovalRequestEntity)

    @Query("UPDATE removal_requests SET status = :status, resolvedBy = :resolvedBy WHERE id = :id")
    suspend fun updateRemovalRequestStatus(id: String, status: String, resolvedBy: String)

    // Audit logs (immutable append-only)
    @Query("SELECT * FROM moderation_audits ORDER BY createdAt DESC")
    fun getAllAudits(): Flow<List<ModerationAuditEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudit(audit: ModerationAuditEntity)

    // Banned terms
    @Query("SELECT * FROM banned_terms")
    fun getAllBannedTerms(): Flow<List<BannedTermEntity>>

    @Query("SELECT * FROM banned_terms")
    suspend fun getAllBannedTermsOnce(): List<BannedTermEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBannedTerm(term: BannedTermEntity)

    @Query("DELETE FROM banned_terms WHERE id = :id")
    suspend fun deleteBannedTerm(id: String)

    // Badges
    @Query("SELECT * FROM badges WHERE userId = :userId ORDER BY awardedAt DESC")
    fun getUserBadges(userId: String): Flow<List<BadgeEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM badges WHERE userId = :userId AND badgeId = :badgeId)")
    suspend fun hasBadge(userId: String, badgeId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadge(badge: BadgeEntity)
}
