package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.BadgeEntity
import com.example.data.local.entity.BannedTermEntity
import com.example.data.local.entity.BlockMuteEntity
import com.example.data.local.entity.ModerationAuditEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.PostEntity
import com.example.data.local.entity.ReactionEntity
import com.example.data.local.entity.RemovalRequestEntity
import com.example.data.local.entity.ReportEntity
import com.example.data.local.entity.RoastEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.data.repository.ExRoastRepository
import com.example.data.safety.SafetyCheckResult
import com.example.data.safety.SafetyChecker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class FeedTab {
    FOR_YOU, HOT, NEW, FOLLOWING
}

data class FilterState(
    val category: String? = null,
    val intensity: String? = null, // Mild, Medium, Extra Crispy
    val timeRange: String = "All Time" // Today, Week, Month, All Time
)

data class UiFeedback(
    val message: String,
    val isError: Boolean = false,
    val isSelfHarmCrisis: Boolean = false
)

class ExRoastViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    val repository = ExRoastRepository(database)

    // Current User
    val currentUserId = repository.currentUserId
    val currentUserProfile: StateFlow<UserProfileEntity?> = repository.currentUserProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Feed Navigation & Filters
    private val _currentFeedTab = MutableStateFlow(FeedTab.FOR_YOU)
    val currentFeedTab: StateFlow<FeedTab> = _currentFeedTab.asStateFlow()

    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    // Navigation route / stack state
    private val _currentScreen = MutableStateFlow("feed") // "feed", "explore", "compose", "activity", "profile", "post_detail", "admin", "settings", "auth"
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    private val _selectedPostId = MutableStateFlow<String?>(null)
    val selectedPostId: StateFlow<String?> = _selectedPostId.asStateFlow()

    private val _selectedUserProfileId = MutableStateFlow<String?>(null)
    val selectedUserProfileId: StateFlow<String?> = _selectedUserProfileId.asStateFlow()

    // Feedback toast/banner
    private val _uiFeedback = MutableStateFlow<UiFeedback?>(null)
    val uiFeedback: StateFlow<UiFeedback?> = _uiFeedback.asStateFlow()

    fun clearFeedback() {
        _uiFeedback.value = null
    }

    fun showFeedback(msg: String, isError: Boolean = false, isSelfHarmCrisis: Boolean = false) {
        _uiFeedback.value = UiFeedback(msg, isError, isSelfHarmCrisis)
    }

    // Settings
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(true)
    val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    private val _reduceMotion = MutableStateFlow(false)
    val reduceMotion: StateFlow<Boolean> = _reduceMotion.asStateFlow()

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setHaptics(enabled: Boolean) {
        _hapticsEnabled.value = enabled
    }

    fun setReduceMotion(enabled: Boolean) {
        _reduceMotion.value = enabled
    }

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setSearchQuery(q: String) {
        _searchQuery.value = q
    }

    // Following Ids & Blocked Ids for filtering
    val blockedUserIds = repository.getBlockedUserIds(currentUserId.value)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val followingIds = repository.getFollowingIds(currentUserId.value)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All published posts combined with active tab and filters
    val feedPosts: StateFlow<List<PostEntity>> = combine(
        repository.allPublishedPosts,
        _currentFeedTab,
        _filterState,
        blockedUserIds,
        followingIds
    ) { posts, tab, filters, blocked, following ->
        var list = posts.filter { it.status == "published" && !blocked.contains(it.authorId) }

        // Filter by Tab
        list = when (tab) {
            FeedTab.HOT -> list.sortedByDescending { it.hotScore }
            FeedTab.NEW -> list.sortedByDescending { it.createdAt }
            FeedTab.FOLLOWING -> list.filter { !it.isAnonymous && following.contains(it.authorId) }
                .sortedByDescending { it.createdAt }
            FeedTab.FOR_YOU -> {
                // For You weighted by user interests, style & recency
                val userInterests = currentUserProfile.value?.interests?.split(",")?.map { it.trim().lowercase() } ?: emptyList()
                list.sortedByDescending { post ->
                    var weight = post.hotScore
                    if (userInterests.contains(post.category.lowercase())) {
                        weight += 20.0
                    }
                    weight
                }
            }
        }

        // Apply Category Filter
        filters.category?.let { cat ->
            list = list.filter { it.category.equals(cat, ignoreCase = true) }
        }

        // Apply Intensity Filter
        filters.intensity?.let { inten ->
            list = list.filter { it.intensity.equals(inten, ignoreCase = true) }
        }

        // Apply Time Range
        val now = System.currentTimeMillis()
        list = when (filters.timeRange) {
            "Today" -> list.filter { now - it.createdAt <= 24 * 3600 * 1000L }
            "Week" -> list.filter { now - it.createdAt <= 7 * 24 * 3600 * 1000L }
            "Month" -> list.filter { now - it.createdAt <= 30 * 24 * 3600 * 1000L }
            else -> list
        }

        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search results
    val searchResults: StateFlow<List<PostEntity>> = _searchQuery.combine(repository.allPublishedPosts) { query, posts ->
        if (query.isBlank()) emptyList()
        else posts.filter {
            it.status == "published" && (
                it.headline.contains(query, ignoreCase = true) ||
                it.body.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Roast of the day
    val roastOfTheDay: StateFlow<PostEntity?> = repository.allPublishedPosts.combine(_selectedPostId) { posts, _ ->
        posts.filter { it.status == "published" }.maxByOrNull { it.hotScore }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Notifications & unread count
    val notifications: StateFlow<List<NotificationEntity>> = repository.getNotifications(currentUserId.value)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationCount: StateFlow<Int> = repository.getUnreadNotificationCount(currentUserId.value)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Top Roasters Leaderboard
    val topRoasters: StateFlow<List<UserProfileEntity>> = repository.getTopRoasters()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Post for Post Detail
    val activePost: StateFlow<PostEntity?> = _selectedPostId.combine(repository.allPublishedPosts) { id, posts ->
        posts.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Roasts for Selected Post
    val postRoasts: StateFlow<List<RoastEntity>> = _selectedPostId.combine(repository.allPublishedPosts) { id, _ ->
        id
    }.combine(database.roastDao().getTopRoastsForPost(_selectedPostId.value ?: "")) { _, roasts ->
        roasts
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Badges for Current User
    val userBadges: StateFlow<List<BadgeEntity>> = repository.getUserBadges(currentUserId.value)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Moderation Data
    val openReports: StateFlow<List<ReportEntity>> = repository.openReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAudits: StateFlow<List<ModerationAuditEntity>> = repository.allAudits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val removalRequests: StateFlow<List<RemovalRequestEntity>> = repository.allRemovalRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bannedTerms: StateFlow<List<BannedTermEntity>> = repository.bannedTerms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Saved post IDs
    val savedPostIds: StateFlow<List<String>> = repository.getSavedPostIds(currentUserId.value)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Initialize default user if not existing
        viewModelScope.launch {
            val existing = database.userProfileDao().getProfileByIdOnce(currentUserId.value)
            if (existing == null) {
                repository.saveProfile(
                    UserProfileEntity(
                        userId = currentUserId.value,
                        username = "anonymous_roaster",
                        displayName = "Anonymous Roaster",
                        bio = "Dating disaster survivor.",
                        roastStyle = "Sarcastic",
                        interests = "First Dates,Red Flags,Texting Crimes",
                        role = "admin" // Give admin/mod capabilities out of the box for testing!
                    )
                )
            }
        }
    }

    // Navigation Helpers
    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun openPostDetail(postId: String) {
        _selectedPostId.value = postId
        _currentScreen.value = "post_detail"
    }

    fun openUserProfile(userId: String) {
        _selectedUserProfileId.value = userId
        _currentScreen.value = "user_profile"
    }

    fun setFeedTab(tab: FeedTab) {
        _currentFeedTab.value = tab
    }

    fun setFilters(category: String?, intensity: String?, timeRange: String = "All Time") {
        _filterState.value = FilterState(category, intensity, timeRange)
    }

    fun clearFilters() {
        _filterState.value = FilterState()
    }

    // Account Switcher for Instant Testing
    fun switchAccount(userId: String) {
        viewModelScope.launch {
            repository.setCurrentUserId(userId)
            val profile = database.userProfileDao().getProfileByIdOnce(userId)
            if (profile == null) {
                val newProfile = when (userId) {
                    "admin_user" -> UserProfileEntity(userId = "admin_user", username = "flame_queen", displayName = "Flame Queen", role = "admin", roastScore = 1420, level = "Roast Legend")
                    "mod_user" -> UserProfileEntity(userId = "mod_user", username = "savage_sam", displayName = "Savage Sam", role = "moderator", roastScore = 650, level = "Inferno")
                    else -> UserProfileEntity(userId = userId, username = "dating_disaster", displayName = "Anonymous Roaster", role = "member", roastScore = 120, level = "Sizzler")
                }
                repository.saveProfile(newProfile)
            }
            showFeedback("Switched user to @${profile?.username ?: userId}")
        }
    }

    // Post creation
    fun publishPost(
        headline: String,
        body: String,
        category: String,
        intensity: String,
        isAnonymous: Boolean,
        anonAlias: String,
        contentWarning: String?,
        imageUrl: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val banned = repository.getBannedTermsOnce()
            // Check headline and body safety
            val check1 = SafetyChecker.checkContent(headline, banned)
            if (!check1.isSafe) {
                showFeedback(check1.failureReason ?: "Safety check failed on headline.", isError = true, isSelfHarmCrisis = check1.isSelfHarmRisk)
                return@launch
            }
            val check2 = SafetyChecker.checkContent(body, banned)
            if (!check2.isSafe) {
                showFeedback(check2.failureReason ?: "Safety check failed on story.", isError = true, isSelfHarmCrisis = check2.isSelfHarmRisk)
                return@launch
            }

            val author = currentUserProfile.value
            val authorId = author?.userId ?: "user_me"

            val newPost = PostEntity(
                id = UUID.randomUUID().toString(),
                authorId = authorId,
                isAnonymous = isAnonymous,
                anonAlias = if (isAnonymous) anonAlias.ifBlank { "Anonymous Roaster" } else (author?.username ?: "anon"),
                headline = headline.trim(),
                body = body.trim(),
                category = category,
                intensity = intensity,
                contentWarning = contentWarning?.ifBlank { null },
                imageUrl = imageUrl,
                createdAt = System.currentTimeMillis()
            )

            repository.createPost(newPost)
            showFeedback("It's out there now. Brace yourself.")
            onSuccess()
            _currentScreen.value = "feed"
        }
    }

    // Add Roast
    fun submitRoast(postId: String, body: String, parentRoastId: String? = null, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (body.isBlank()) return@launch

            val banned = repository.getBannedTermsOnce()
            val check = SafetyChecker.checkContent(body, banned)
            if (!check.isSafe) {
                showFeedback(check.failureReason ?: "Roast content violates safety rules.", isError = true, isSelfHarmCrisis = check.isSelfHarmRisk)
                return@launch
            }

            val post = database.postDao().getPostByIdOnce(postId) ?: return@launch
            val author = currentUserProfile.value
            val authorId = author?.userId ?: "user_me"
            val authorName = author?.username ?: "Anonymous"
            val authorAvatar = author?.avatarUrl ?: ""

            val newRoast = RoastEntity(
                id = UUID.randomUUID().toString(),
                postId = postId,
                authorId = authorId,
                authorName = authorName,
                authorAvatar = authorAvatar,
                parentRoastId = parentRoastId,
                body = body.trim(),
                clientRequestId = UUID.randomUUID().toString(),
                createdAt = System.currentTimeMillis()
            )

            repository.addRoast(newRoast, post.authorId, post.headline)
            showFeedback("Roast delivered!")
            onSuccess()
        }
    }

    // Reaction
    fun reactToPost(postId: String, reactionType: String) {
        viewModelScope.launch {
            val user = currentUserProfile.value
            val userId = user?.userId ?: "user_me"
            val userName = user?.username ?: "Anonymous"
            repository.toggleReaction(userId, userName, "post", postId, reactionType)
        }
    }

    fun reactToRoast(roastId: String, reactionType: String) {
        viewModelScope.launch {
            val user = currentUserProfile.value
            val userId = user?.userId ?: "user_me"
            val userName = user?.username ?: "Anonymous"
            repository.toggleReaction(userId, userName, "roast", roastId, reactionType)
        }
    }

    // Save
    fun toggleSavePost(postId: String) {
        viewModelScope.launch {
            val userId = currentUserId.value
            if (savedPostIds.value.contains(postId)) {
                repository.removeSave(userId, postId)
                showFeedback("Removed from saved stories.")
            } else {
                repository.toggleSave(userId, postId)
                showFeedback("Story saved to private collection.")
            }
        }
    }

    // Follow
    fun toggleFollow(targetUserId: String) {
        viewModelScope.launch {
            val current = currentUserId.value
            if (followingIds.value.contains(targetUserId)) {
                repository.unfollow(current, targetUserId)
                showFeedback("Unfollowed user.")
            } else {
                repository.toggleFollow(current, targetUserId)
                showFeedback("Now following!")
            }
        }
    }

    // Block / Mute
    fun blockUser(targetUserId: String, targetUsername: String) {
        viewModelScope.launch {
            repository.blockUser(currentUserId.value, targetUserId, targetUsername)
            showFeedback("Blocked @$targetUsername. Their content will no longer appear.")
        }
    }

    fun unblockUser(targetUserId: String) {
        viewModelScope.launch {
            repository.unblockUser(currentUserId.value, targetUserId)
            showFeedback("User unblocked.")
        }
    }

    // Author Controls on own post
    fun hideRoast(roastId: String) {
        viewModelScope.launch {
            repository.hideRoastByAuthor(roastId)
            showFeedback("Roast hidden from post.")
        }
    }

    fun pinRoast(postId: String, roastId: String) {
        viewModelScope.launch {
            repository.setPinnedRoast(postId, roastId)
            showFeedback("Roast pinned to top.")
        }
    }

    fun toggleRoastsMode(postId: String, mode: String) {
        viewModelScope.launch {
            repository.setPostRoastsMode(postId, mode)
            showFeedback("Roasts mode set to $mode.")
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            repository.deletePost(postId)
            showFeedback("Post deleted.")
            _currentScreen.value = "feed"
        }
    }

    // Report
    fun submitReport(targetType: String, targetId: String, snippet: String, reason: String, note: String) {
        viewModelScope.launch {
            repository.submitReport(
                reporterId = currentUserId.value,
                targetType = targetType,
                targetId = targetId,
                targetSnippet = snippet,
                reason = reason,
                note = note
            )
            showFeedback("Thanks, we'll review this report shortly.")
        }
    }

    // "This is about me" removal request
    fun submitRemovalRequest(email: String, postId: String, explanation: String) {
        viewModelScope.launch {
            repository.submitRemovalRequest(email, postId, explanation)
            showFeedback("Removal request received. This story has been temporarily hidden pending review.")
        }
    }

    // Moderation Queue actions
    fun resolveReportAction(
        reportId: String,
        action: String,
        reason: String,
        targetType: String,
        targetId: String
    ) {
        viewModelScope.launch {
            val mod = currentUserProfile.value
            repository.resolveReport(
                reportId = reportId,
                moderatorId = mod?.userId ?: "mod_user",
                moderatorName = mod?.username ?: "moderator",
                action = action,
                reason = reason,
                targetType = targetType,
                targetId = targetId
            )
            showFeedback("Report actioned: $action")
        }
    }

    fun addBannedTerm(pattern: String, isRegex: Boolean, severity: String) {
        viewModelScope.launch {
            val user = currentUserProfile.value?.username ?: "admin"
            repository.addBannedTerm(pattern, isRegex, severity, user)
            showFeedback("Banned term added.")
        }
    }

    fun removeBannedTerm(id: String) {
        viewModelScope.launch {
            repository.removeBannedTerm(id)
            showFeedback("Banned term removed.")
        }
    }

    // Dev / Seeding
    fun seedDemoData() {
        viewModelScope.launch {
            repository.seedDemoCommunityData()
            showFeedback("Demo stories & roasters loaded successfully!")
        }
    }

    fun markNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead(currentUserId.value)
        }
    }

    // Data Export (GDPR/NDPR JSON)
    fun exportUserDataJson(): String {
        val user = currentUserProfile.value ?: return "{}"
        return """
        {
          "user_id": "${user.userId}",
          "username": "${user.username}",
          "display_name": "${user.displayName}",
          "bio": "${user.bio}",
          "roast_style": "${user.roastStyle}",
          "interests": "${user.interests}",
          "roast_score": ${user.roastScore},
          "level": "${user.level}",
          "streak": ${user.currentStreak},
          "longest_streak": ${user.longestStreak},
          "export_timestamp": ${System.currentTimeMillis()},
          "format": "GDPR_DATA_ARCHIVE"
        }
        """.trimIndent()
    }
}
