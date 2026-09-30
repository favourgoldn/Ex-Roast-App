package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.BadgeEntity
import com.example.data.local.entity.BannedTermEntity
import com.example.data.local.entity.BlockMuteEntity
import com.example.data.local.entity.FollowEntity
import com.example.data.local.entity.ModerationAuditEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.PostEntity
import com.example.data.local.entity.ReactionEntity
import com.example.data.local.entity.RemovalRequestEntity
import com.example.data.local.entity.ReportEntity
import com.example.data.local.entity.RoastEntity
import com.example.data.local.entity.SaveEntity
import com.example.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class ExRoastRepository(private val database: AppDatabase) {
    private val userProfileDao = database.userProfileDao()
    private val postDao = database.postDao()
    private val roastDao = database.roastDao()
    private val reactionDao = database.reactionDao()
    private val socialDao = database.socialDao()
    private val moderationDao = database.moderationDao()

    // Current active user ID
    private val _currentUserId = MutableStateFlow("user_me")
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    fun setCurrentUserId(id: String) {
        _currentUserId.value = id
    }

    // Profiles
    val currentUserProfile: Flow<UserProfileEntity?> = userProfileDao.getProfileById(_currentUserId.value)
    fun getProfile(userId: String): Flow<UserProfileEntity?> = userProfileDao.getProfileById(userId)
    fun getTopRoasters(): Flow<List<UserProfileEntity>> = userProfileDao.getTopRoasters()
    fun searchUsers(query: String): Flow<List<UserProfileEntity>> = userProfileDao.searchUsers(query)
    fun getAllProfiles(): Flow<List<UserProfileEntity>> = userProfileDao.getAllProfiles()

    suspend fun saveProfile(profile: UserProfileEntity) {
        val calculatedLevel = calculateLevel(profile.roastScore)
        userProfileDao.insertOrUpdate(profile.copy(level = calculatedLevel))
    }

    suspend fun updateUserRole(userId: String, newRole: String) {
        userProfileDao.updateUserRole(userId, newRole)
    }

    suspend fun updateUserStatus(userId: String, status: String) {
        userProfileDao.updateUserStatus(userId, status)
    }

    suspend fun deleteUserAccount(userId: String) {
        userProfileDao.deleteUser(userId)
    }

    // Posts
    val allPublishedPosts: Flow<List<PostEntity>> = postDao.getAllPublishedPosts()
    val hotPosts: Flow<List<PostEntity>> = postDao.getHotPosts()

    fun getPost(id: String): Flow<PostEntity?> = postDao.getPostById(id)
    fun getPostsByAuthor(authorId: String): Flow<List<PostEntity>> = postDao.getPostsByAuthor(authorId)
    fun getPublicPostsByAuthor(authorId: String): Flow<List<PostEntity>> = postDao.getPublicPostsByAuthor(authorId)
    fun getPostsByCategory(category: String): Flow<List<PostEntity>> = postDao.getPostsByCategory(category)
    fun searchPosts(query: String): Flow<List<PostEntity>> = postDao.searchPosts(query)
    fun getPostsByTag(tag: String): Flow<List<PostEntity>> = postDao.getPostsByTag(tag)

    suspend fun createPost(post: PostEntity) {
        val initialHot = calculateHotScore(0, 0, 0, 0, post.createdAt)
        postDao.insertPost(post.copy(hotScore = initialHot))
        // Award first post badge if not yet given
        checkAndAwardBadge(post.authorId, "story_starter", "Story Starter", "Shared your first dating disaster with the internet.", "flame")
        // Update user activity streak
        updateUserStreak(post.authorId)
    }

    suspend fun updatePost(post: PostEntity) {
        postDao.updatePost(post.copy(editedAt = System.currentTimeMillis()))
    }

    suspend fun deletePost(id: String) {
        postDao.updatePostStatus(id, "deleted")
    }

    suspend fun setPostRoastsMode(id: String, mode: String) {
        postDao.setRoastsMode(id, mode)
    }

    suspend fun setPinnedRoast(postId: String, roastId: String?) {
        postDao.setPinnedRoast(postId, roastId)
        if (roastId != null) {
            roastDao.setPinned(roastId, true)
        }
    }

    // Roasts (Comments)
    fun getTopRoasts(postId: String): Flow<List<RoastEntity>> = roastDao.getTopRoastsForPost(postId)
    fun getNewestRoasts(postId: String): Flow<List<RoastEntity>> = roastDao.getNewestRoastsForPost(postId)
    fun getOldestRoasts(postId: String): Flow<List<RoastEntity>> = roastDao.getOldestRoastsForPost(postId)
    fun getComebacks(roastId: String): Flow<List<RoastEntity>> = roastDao.getComebacksForRoast(roastId)
    fun getRoastsByAuthor(authorId: String): Flow<List<RoastEntity>> = roastDao.getRoastsByAuthor(authorId)

    suspend fun addRoast(roast: RoastEntity, postAuthorId: String, postHeadline: String) {
        roastDao.insertRoast(roast)
        if (roast.parentRoastId != null) {
            roastDao.adjustReplyCount(roast.parentRoastId, 1)
        } else {
            postDao.adjustRoastCount(roast.postId, 1)
        }

        // Notify post author (if not self)
        if (postAuthorId != roast.authorId && postAuthorId.isNotEmpty()) {
            database.socialDao().insertNotification(
                NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    recipientId = postAuthorId,
                    type = if (roast.parentRoastId != null) "comeback" else "roast",
                    actorId = roast.authorId,
                    actorName = roast.authorName,
                    actorAvatar = roast.authorAvatar,
                    postId = roast.postId,
                    roastId = roast.id,
                    message = "${roast.authorName} dropped a roast on '$postHeadline': \"${roast.body.take(40)}...\"",
                    groupKey = "roast_${roast.postId}",
                    count = 1
                )
            )
        }

        // Award badge for first roast
        checkAndAwardBadge(roast.authorId, "first_roast", "First Blood", "Delivered your first roast.", "skull")
        updateUserStreak(roast.authorId)
        recalculateTopRoastAndHotScore(roast.postId)
    }

    suspend fun deleteRoast(roast: RoastEntity) {
        roastDao.updateRoastStatus(roast.id, "deleted")
        if (roast.parentRoastId != null) {
            roastDao.adjustReplyCount(roast.parentRoastId, -1)
        } else {
            postDao.adjustRoastCount(roast.postId, -1)
        }
        recalculateTopRoastAndHotScore(roast.postId)
    }

    suspend fun hideRoastByAuthor(roastId: String) {
        roastDao.updateRoastStatus(roastId, "hidden_by_author")
    }

    // Reactions
    fun getReactions(targetType: String, targetId: String): Flow<List<ReactionEntity>> =
        reactionDao.getReactionsForTarget(targetType, targetId)

    fun getUserReaction(userId: String, targetType: String, targetId: String): Flow<ReactionEntity?> =
        reactionDao.getUserReaction(userId, targetType, targetId)

    suspend fun toggleReaction(
        userId: String,
        userName: String,
        targetType: String,
        targetId: String,
        reactionType: String
    ) {
        val existing = reactionDao.getUserReactionOnce(userId, targetType, targetId)
        if (existing != null) {
            if (existing.reactionType == reactionType) {
                // Remove reaction
                reactionDao.deleteReaction(userId, targetType, targetId)
                if (targetType == "post") {
                    postDao.adjustReactionCount(targetId, -1)
                    recalculatePostHotScore(targetId)
                } else if (targetType == "roast") {
                    roastDao.adjustReactionCount(targetId, -1)
                }
            } else {
                // Change reaction type
                reactionDao.insertReaction(existing.copy(reactionType = reactionType))
            }
        } else {
            // New reaction
            reactionDao.insertReaction(
                ReactionEntity(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    userName = userName,
                    targetType = targetType,
                    targetId = targetId,
                    reactionType = reactionType
                )
            )
            if (targetType == "post") {
                postDao.adjustReactionCount(targetId, 1)
                userProfileDao.incrementRoastScore(userId, 1)
                recalculatePostHotScore(targetId)
            } else if (targetType == "roast") {
                roastDao.adjustReactionCount(targetId, 1)
                // Roast author gets +2 Roast Score
                val roast = roastDao.getRoastByIdOnce(targetId)
                if (roast != null) {
                    userProfileDao.incrementRoastScore(roast.authorId, 2)
                    recalculateTopRoastAndHotScore(roast.postId)
                }
            }
        }
    }

    // Recalculates Top Roast crown and Hot score
    private suspend fun recalculateTopRoastAndHotScore(postId: String) {
        val roasts = roastDao.getAllRoastsForPostOnce(postId)
            .filter { it.parentRoastId == null && it.status == "visible" }
        if (roasts.size >= 3) {
            val top = roasts.maxByOrNull { it.reactionCount }
            if (top != null && top.reactionCount > 0) {
                roastDao.setTopRoast(postId, top.id)
                // Award crown score bonus (+25) if not already crowned
                if (!top.isTopRoast) {
                    userProfileDao.incrementRoastScore(top.authorId, 25)
                    checkAndAwardBadge(top.authorId, "top_roast", "Crown of Thorns", "Awarded Top Roast by the community.", "gold_crown")
                }
            }
        }
        recalculatePostHotScore(postId)
    }

    private suspend fun recalculatePostHotScore(postId: String) {
        val post = postDao.getPostByIdOnce(postId) ?: return
        val newHot = calculateHotScore(
            roasts = post.roastCount,
            reactions = post.reactionCount,
            shares = post.shareCount,
            saves = post.saveCount,
            createdAt = post.createdAt
        )
        postDao.updateHotScore(postId, newHot)
    }

    /**
     * Hot ranking formula specified in Section 5.3:
     * (roasts*3 + reactions*1 + shares*4 + saves*2) / (hours_since_post + 2)^1.4
     */
    private fun calculateHotScore(roasts: Int, reactions: Int, shares: Int, saves: Int, createdAt: Long): Double {
        val numerator = (roasts * 3) + (reactions * 1) + (shares * 4) + (saves * 2)
        val hoursSince = ((System.currentTimeMillis() - createdAt) / (1000.0 * 60 * 60)).coerceAtLeast(0.0)
        val denominator = Math.pow(hoursSince + 2.0, 1.4)
        return numerator / denominator
    }

    private fun calculateLevel(score: Int): String {
        return when {
            score >= 1000 -> "Roast Legend"
            score >= 500 -> "Inferno"
            score >= 200 -> "Flame Thrower"
            score >= 50 -> "Sizzler"
            else -> "Rookie"
        }
    }

    private suspend fun updateUserStreak(userId: String) {
        val profile = userProfileDao.getProfileByIdOnce(userId) ?: return
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L
        val daysSinceLastActive = (now - profile.lastActiveDate) / oneDayMillis

        val newStreak = when {
            daysSinceLastActive < 1 -> profile.currentStreak // Already active today
            daysSinceLastActive in 1..2 -> profile.currentStreak + 1
            profile.streakFreezes > 0 -> {
                // Use streak freeze
                userProfileDao.insertOrUpdate(profile.copy(streakFreezes = profile.streakFreezes - 1, lastActiveDate = now))
                return
            }
            else -> 1
        }
        val longest = maxOf(newStreak, profile.longestStreak)
        userProfileDao.insertOrUpdate(
            profile.copy(
                currentStreak = newStreak,
                longestStreak = longest,
                lastActiveDate = now
            )
        )
        if (newStreak >= 7) {
            checkAndAwardBadge(userId, "streak_7", "7-Day Inferno", "Kept the fire burning for 7 straight days.", "fire_streak")
        }
    }

    private suspend fun checkAndAwardBadge(userId: String, badgeId: String, title: String, description: String, iconName: String) {
        if (!moderationDao.hasBadge(userId, badgeId)) {
            moderationDao.insertBadge(
                BadgeEntity(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    badgeId = badgeId,
                    title = title,
                    description = description,
                    iconName = iconName
                )
            )
            // Send in-app notification
            socialDao.insertNotification(
                NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    recipientId = userId,
                    type = "badge",
                    message = "Badge Unlocked: $title! $description",
                    groupKey = "badge_$badgeId"
                )
            )
        }
    }

    // Social Graph
    fun getFollowingIds(userId: String): Flow<List<String>> = socialDao.getFollowingIds(userId)
    fun getFollowerCount(userId: String): Flow<Int> = socialDao.getFollowerCount(userId)
    fun getFollowingCount(userId: String): Flow<Int> = socialDao.getFollowingCount(userId)
    fun getFollow(followerId: String, followingId: String): Flow<FollowEntity?> = socialDao.getFollow(followerId, followingId)

    suspend fun toggleFollow(followerId: String, followingId: String) {
        val targetProfile = userProfileDao.getProfileByIdOnce(followingId)
        val status = if (targetProfile?.isPrivate == true) "pending" else "accepted"
        socialDao.insertFollow(
            FollowEntity(
                id = UUID.randomUUID().toString(),
                followerId = followerId,
                followingId = followingId,
                status = status
            )
        )
    }

    suspend fun unfollow(followerId: String, followingId: String) {
        socialDao.deleteFollow(followerId, followingId)
    }

    // Blocks & Mutes
    fun getBlockedUserIds(userId: String): Flow<List<String>> = socialDao.getBlockedUserIds(userId)
    fun getMutedUserIds(userId: String): Flow<List<String>> = socialDao.getMutedUserIds(userId)
    fun getBlockedUsers(userId: String): Flow<List<BlockMuteEntity>> = socialDao.getBlockedUsers(userId)
    fun getMutedUsers(userId: String): Flow<List<BlockMuteEntity>> = socialDao.getMutedUsers(userId)

    suspend fun blockUser(userId: String, targetUserId: String, targetUsername: String) {
        socialDao.insertBlockMute(
            BlockMuteEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                targetUserId = targetUserId,
                targetUsername = targetUsername,
                isBlock = true
            )
        )
        // Also remove any follow link
        socialDao.deleteFollow(userId, targetUserId)
        socialDao.deleteFollow(targetUserId, userId)
    }

    suspend fun unblockUser(userId: String, targetUserId: String) {
        socialDao.deleteBlockMute(userId, targetUserId, true)
    }

    suspend fun muteUser(userId: String, targetUserId: String, targetUsername: String) {
        socialDao.insertBlockMute(
            BlockMuteEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                targetUserId = targetUserId,
                targetUsername = targetUsername,
                isBlock = false
            )
        )
    }

    suspend fun unmuteUser(userId: String, targetUserId: String) {
        socialDao.deleteBlockMute(userId, targetUserId, false)
    }

    // Saves
    fun getSavedPostIds(userId: String): Flow<List<String>> = socialDao.getSavedPostIds(userId)
    fun isPostSaved(userId: String, postId: String): Flow<Boolean> = socialDao.isPostSaved(userId, postId)
    fun getCollections(userId: String): Flow<List<String>> = socialDao.getCollections(userId)

    suspend fun toggleSave(userId: String, postId: String, collectionName: String = "Saved") {
        socialDao.insertSave(
            SaveEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                postId = postId,
                collectionName = collectionName
            )
        )
        postDao.adjustSaveCount(postId, 1)
        recalculatePostHotScore(postId)
    }

    suspend fun removeSave(userId: String, postId: String) {
        socialDao.deleteSave(userId, postId)
        postDao.adjustSaveCount(postId, -1)
        recalculatePostHotScore(postId)
    }

    // Notifications
    fun getNotifications(recipientId: String): Flow<List<NotificationEntity>> = socialDao.getNotifications(recipientId)
    fun getUnreadNotificationCount(recipientId: String): Flow<Int> = socialDao.getUnreadNotificationCount(recipientId)
    suspend fun markAllNotificationsRead(recipientId: String) = socialDao.markAllNotificationsAsRead(recipientId)
    suspend fun markNotificationRead(id: String) = socialDao.markNotificationAsRead(id)

    // Moderation & Trust/Safety
    val openReports: Flow<List<ReportEntity>> = moderationDao.getOpenReports()
    val allReports: Flow<List<ReportEntity>> = moderationDao.getAllReports()
    val openReportCount: Flow<Int> = moderationDao.getOpenReportCount()
    val allAudits: Flow<List<ModerationAuditEntity>> = moderationDao.getAllAudits()
    val allRemovalRequests: Flow<List<RemovalRequestEntity>> = moderationDao.getAllRemovalRequests()
    val bannedTerms: Flow<List<BannedTermEntity>> = moderationDao.getAllBannedTerms()
    suspend fun getBannedTermsOnce(): List<BannedTermEntity> = moderationDao.getAllBannedTermsOnce()

    suspend fun submitReport(
        reporterId: String,
        targetType: String,
        targetId: String,
        targetSnippet: String,
        reason: String,
        note: String
    ) {
        val priority = if (reason in listOf("Real person identified", "Threats or violence", "Self-harm")) "high" else "medium"
        moderationDao.insertReport(
            ReportEntity(
                id = UUID.randomUUID().toString(),
                reporterId = reporterId,
                targetType = targetType,
                targetId = targetId,
                targetSnippet = targetSnippet,
                reason = reason,
                note = note,
                priority = priority
            )
        )
    }

    suspend fun submitRemovalRequest(email: String, postId: String, explanation: String) {
        moderationDao.insertRemovalRequest(
            RemovalRequestEntity(
                id = UUID.randomUUID().toString(),
                requesterEmail = email,
                postId = postId,
                explanation = explanation,
                status = "hidden_pending_review"
            )
        )
        // Automatically hide post pending review per Section 9
        postDao.updatePostStatus(postId, "hidden_pending_review")
    }

    suspend fun resolveReport(
        reportId: String,
        moderatorId: String,
        moderatorName: String,
        action: String, // dismiss, remove_content, warn_user, temp_ban_1, temp_ban_7, temp_ban_30, ban_perm
        reason: String,
        targetType: String,
        targetId: String
    ) {
        moderationDao.resolveReport(reportId, "actioned", moderatorId, System.currentTimeMillis())
        // Record immutable audit entry
        moderationDao.insertAudit(
            ModerationAuditEntity(
                id = UUID.randomUUID().toString(),
                moderatorId = moderatorId,
                moderatorName = moderatorName,
                action = action,
                targetType = targetType,
                targetId = targetId,
                reason = reason
            )
        )

        when (action) {
            "remove_content" -> {
                if (targetType == "post") postDao.updatePostStatus(targetId, "removed")
                else if (targetType == "roast") roastDao.updateRoastStatus(targetId, "removed")
            }
            "temp_ban_1", "temp_ban_7", "temp_ban_30" -> {
                userProfileDao.updateUserStatus(targetId, "suspended")
            }
            "ban_perm" -> {
                userProfileDao.updateUserStatus(targetId, "banned")
            }
        }
    }

    suspend fun addBannedTerm(pattern: String, isRegex: Boolean, severity: String, createdBy: String) {
        moderationDao.insertBannedTerm(
            BannedTermEntity(
                id = UUID.randomUUID().toString(),
                pattern = pattern,
                isRegex = isRegex,
                severity = severity,
                createdBy = createdBy
            )
        )
    }

    suspend fun removeBannedTerm(id: String) {
        moderationDao.deleteBannedTerm(id)
    }

    fun getUserBadges(userId: String): Flow<List<BadgeEntity>> = moderationDao.getUserBadges(userId)

    /**
     * Seeds initial sample stories and users ONLY when explicitly requested by user
     * via the Settings Dev tool. The database remains empty on fresh install per spec!
     */
    suspend fun seedDemoCommunityData() {
        val admin = UserProfileEntity(
            userId = "admin_user",
            username = "flame_queen",
            displayName = "Flame Queen",
            bio = "Spilling hot tea since 2019. Chief roaster & safety guardian.",
            avatarUrl = "",
            roastStyle = "Savage",
            interests = "First Dates,Red Flags,Cheaters",
            role = "admin",
            roastScore = 1420,
            level = "Roast Legend",
            currentStreak = 14,
            longestStreak = 28
        )
        val mod = UserProfileEntity(
            userId = "mod_user",
            username = "savage_sam",
            displayName = "Savage Sam",
            bio = "Dry wit connoisseur. Keep it clever, keep it anonymized.",
            avatarUrl = "",
            roastStyle = "Dry Wit",
            interests = "Texting Crimes,Situationships,Gifts Gone Wrong",
            role = "moderator",
            roastScore = 650,
            level = "Inferno",
            currentStreak = 6,
            longestStreak = 12
        )
        val member = UserProfileEntity(
            userId = "user_me",
            username = "dating_disaster",
            displayName = "Anonymous Roaster",
            bio = "Recovering romantic. Here to turn my emotional damage into entertainment.",
            avatarUrl = "",
            roastStyle = "Sarcastic",
            interests = "First Dates,Red Flags,Breakup Fails",
            role = "member",
            roastScore = 120,
            level = "Sizzler",
            currentStreak = 3,
            longestStreak = 5
        )

        userProfileDao.insertOrUpdate(admin)
        userProfileDao.insertOrUpdate(mod)
        userProfileDao.insertOrUpdate(member)

        val story1 = PostEntity(
            id = "story_1",
            authorId = "user_me",
            isAnonymous = true,
            anonAlias = "The Spreadsheet Survivor",
            headline = "He sent me a PayPal invoice for half a guacamole after our 2nd date",
            body = "We went to a decent taco spot. He ordered a side of guac that I had literally two chips from. Next morning at 8:03 AM sharp, I get an official PayPal invoice for '$3.87 — Guacamole Allocation (50%)'. When I didn't pay it within 4 hours, he sent a follow-up email marking it 'Second Notice'. I paid it with a note saying 'Consider this an investment in my singlehood'.",
            category = "First Dates",
            intensity = "Extra Crispy",
            roastCount = 4,
            reactionCount = 38,
            saveCount = 12,
            shareCount = 9,
            hotScore = 45.2,
            createdAt = System.currentTimeMillis() - (3 * 3600 * 1000L)
        )

        val story2 = PostEntity(
            id = "story_2",
            authorId = "mod_user",
            isAnonymous = false,
            headline = "He asked me to whisper his mother's chicken parmesan recipe during our anniversary",
            body = "Three years together. We're at a candlelight dinner in Rome. He looks deeply into my eyes, grabs both my hands, and says: 'Before we go back to the hotel, recite mama's breading ratio. If you love me, you memorized the breadcrumbs.' I guessed panko. He sighed, took his hands back, and didn't speak to me until Frankfurt airport.",
            category = "Red Flags",
            intensity = "Medium",
            roastCount = 3,
            reactionCount = 52,
            saveCount = 18,
            shareCount = 14,
            hotScore = 58.7,
            createdAt = System.currentTimeMillis() - (8 * 3600 * 1000L)
        )

        val story3 = PostEntity(
            id = "story_3",
            authorId = "admin_user",
            isAnonymous = true,
            anonAlias = "The Silent Driver",
            headline = "He ghosted me after 6 months because my Spotify Wrapped had 'too much acoustic guitar'",
            body = "We practically lived together. December comes around, we exchange Spotify Wrapped. He stared at my top genre ('Acoustic Folk') for 4 straight minutes in absolute silence. Packed his weekend duffel, said 'I can't build a future with someone who tolerates fingerpicking', and blocked me on everything. Even Venmo.",
            category = "Breakup Fails",
            intensity = "Mild",
            roastCount = 3,
            reactionCount = 29,
            saveCount = 8,
            shareCount = 6,
            hotScore = 32.1,
            createdAt = System.currentTimeMillis() - (18 * 3600 * 1000L)
        )

        postDao.insertPost(story1)
        postDao.insertPost(story2)
        postDao.insertPost(story3)

        // Seed roasts for story 1
        val r1 = RoastEntity(
            id = "roast_1_1",
            postId = "story_1",
            authorId = "admin_user",
            authorName = "flame_queen",
            authorAvatar = "",
            body = "Should've sent back a counter-invoice for $150 emotional distress and consultation fees for tolerating his spreadsheet personality.",
            reactionCount = 24,
            isTopRoast = true,
            createdAt = System.currentTimeMillis() - (2 * 3600 * 1000L)
        )
        val r2 = RoastEntity(
            id = "roast_1_2",
            postId = "story_1",
            authorId = "mod_user",
            authorName = "savage_sam",
            authorAvatar = "",
            body = "Bro has an amortization schedule for love. Hope he files for romantic bankruptcy.",
            reactionCount = 16,
            createdAt = System.currentTimeMillis() - (2 * 3600 * 1000L + 500000L)
        )
        val r3 = RoastEntity(
            id = "roast_1_3",
            postId = "story_1",
            authorId = "mod_user",
            authorName = "savage_sam",
            authorAvatar = "",
            body = "The chip-to-cost ratio alone deserved an audit. Glad you liquidated that asset immediately.",
            reactionCount = 9,
            createdAt = System.currentTimeMillis() - (1 * 3600 * 1000L)
        )
        roastDao.insertRoast(r1)
        roastDao.insertRoast(r2)
        roastDao.insertRoast(r3)

        // Seed badges for admin & user
        moderationDao.insertBadge(BadgeEntity(id = "b1", userId = "user_me", badgeId = "story_starter", title = "Story Starter", description = "Shared your first dating disaster.", iconName = "flame"))
        moderationDao.insertBadge(BadgeEntity(id = "b2", userId = "user_me", badgeId = "first_roast", title = "First Blood", description = "Delivered your first roast.", iconName = "skull"))
    }
}
