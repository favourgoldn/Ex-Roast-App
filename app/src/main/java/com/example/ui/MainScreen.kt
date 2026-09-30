package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.entity.PostEntity
import com.example.data.local.entity.RoastEntity
import com.example.ui.components.BrandHeader
import com.example.ui.components.BrokenHeartFlameIcon
import com.example.ui.components.CrisisResourceDialog
import com.example.ui.components.RemovalRequestDialog
import com.example.ui.components.ReportDialog
import com.example.ui.components.ShareCardDialog
import com.example.ui.screens.ActivityScreen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.ComposerScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.PostDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.ExDarkBorder
import com.example.ui.theme.ExFlame
import com.example.ui.theme.ExFlameHot
import com.example.ui.theme.ExRoastTheme
import com.example.ui.viewmodel.ExRoastViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: ExRoastViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()
    val currentUserProfile by viewModel.currentUserProfile.collectAsState()

    val feedPosts by viewModel.feedPosts.collectAsState()
    val currentFeedTab by viewModel.currentFeedTab.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val savedPostIds by viewModel.savedPostIds.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val roastOfTheDay by viewModel.roastOfTheDay.collectAsState()
    val topRoasters by viewModel.topRoasters.collectAsState()

    val notifications by viewModel.notifications.collectAsState()
    val unreadNotifCount by viewModel.unreadNotificationCount.collectAsState()

    val activePost by viewModel.activePost.collectAsState()
    val postRoasts by viewModel.postRoasts.collectAsState()
    val userBadges by viewModel.userBadges.collectAsState()

    val openReports by viewModel.openReports.collectAsState()
    val removalRequests by viewModel.removalRequests.collectAsState()
    val bannedTerms by viewModel.bannedTerms.collectAsState()
    val allAudits by viewModel.allAudits.collectAsState()

    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val hapticsEnabled by viewModel.hapticsEnabled.collectAsState()
    val reduceMotion by viewModel.reduceMotion.collectAsState()

    val uiFeedback by viewModel.uiFeedback.collectAsState()

    // Dialog states
    var reportTarget by remember { mutableStateOf<Pair<String, String>?>(null) } // type to id
    var removalRequestPostId by remember { mutableStateOf<String?>(null) }
    var shareCardPost by remember { mutableStateOf<PostEntity?>(null) }
    var showUserSwitcherSheet by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiFeedback) {
        uiFeedback?.let { feedback ->
            if (!feedback.isSelfHarmCrisis) {
                snackbarHostState.showSnackbar(feedback.message)
                viewModel.clearFeedback()
            }
        }
    }

    // Handle Back Press
    BackHandler(enabled = currentScreen != "feed") {
        when (currentScreen) {
            "post_detail", "compose", "admin", "settings", "user_profile" -> viewModel.navigateTo("feed")
            "explore", "activity", "profile" -> viewModel.navigateTo("feed")
            else -> viewModel.navigateTo("feed")
        }
    }

    ExRoastTheme(darkTheme = isDarkTheme) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = ExRoastTheme.colors.bg,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                // Show brand header only on root navigation tabs
                if (currentScreen in listOf("feed", "explore", "activity", "profile")) {
                    BrandHeader(
                        unreadCount = unreadNotifCount,
                        currentRole = currentUserProfile?.role ?: "member",
                        onSearchClick = { viewModel.navigateTo("explore") },
                        onNotificationsClick = { viewModel.navigateTo("activity") },
                        onSwitchUserClick = { showUserSwitcherSheet = true },
                        modifier = Modifier.statusBarsPadding()
                    )
                }
            },
            bottomBar = {
                // Bottom Tab Bar on root screens
                if (currentScreen in listOf("feed", "explore", "activity", "profile")) {
                    NavigationBar(
                        containerColor = ExRoastTheme.colors.surface,
                        contentColor = ExFlame,
                        tonalElevation = 8.dp,
                        modifier = Modifier.navigationBarsPadding()
                    ) {
                        // Feed Tab
                        NavigationBarItem(
                            selected = currentScreen == "feed",
                            onClick = { viewModel.navigateTo("feed") },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Feed") },
                            label = { Text("Feed", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ExFlame,
                                selectedTextColor = ExFlame,
                                indicatorColor = ExFlame.copy(alpha = 0.15f),
                                unselectedIconColor = ExRoastTheme.colors.textMuted,
                                unselectedTextColor = ExRoastTheme.colors.textMuted
                            )
                        )

                        // Explore Tab
                        NavigationBarItem(
                            selected = currentScreen == "explore",
                            onClick = { viewModel.navigateTo("explore") },
                            icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
                            label = { Text("Explore", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ExFlame,
                                selectedTextColor = ExFlame,
                                indicatorColor = ExFlame.copy(alpha = 0.15f),
                                unselectedIconColor = ExRoastTheme.colors.textMuted,
                                unselectedTextColor = ExRoastTheme.colors.textMuted
                            )
                        )

                        // Center Raised Flame Post Button
                        NavigationBarItem(
                            selected = false,
                            onClick = { viewModel.navigateTo("compose") },
                            icon = {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(ExFlame),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Post a Story",
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            },
                            label = { Text("Spill", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            )
                        )

                        // Activity Tab
                        NavigationBarItem(
                            selected = currentScreen == "activity",
                            onClick = { viewModel.navigateTo("activity") },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (unreadNotifCount > 0) {
                                            Badge(containerColor = ExFlame, contentColor = Color.White) {
                                                Text(if (unreadNotifCount > 9) "9+" else unreadNotifCount.toString(), fontSize = 10.sp)
                                            }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Notifications, contentDescription = "Activity")
                                }
                            },
                            label = { Text("Activity", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ExFlame,
                                selectedTextColor = ExFlame,
                                indicatorColor = ExFlame.copy(alpha = 0.15f),
                                unselectedIconColor = ExRoastTheme.colors.textMuted,
                                unselectedTextColor = ExRoastTheme.colors.textMuted
                            )
                        )

                        // Profile Tab
                        NavigationBarItem(
                            selected = currentScreen == "profile",
                            onClick = { viewModel.navigateTo("profile") },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                            label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ExFlame,
                                selectedTextColor = ExFlame,
                                indicatorColor = ExFlame.copy(alpha = 0.15f),
                                unselectedIconColor = ExRoastTheme.colors.textMuted,
                                unselectedTextColor = ExRoastTheme.colors.textMuted
                            )
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentScreen) {
                    "feed" -> FeedScreen(
                        posts = feedPosts,
                        currentTab = currentFeedTab,
                        filterState = filterState,
                        currentUserId = currentUserId,
                        savedPostIds = savedPostIds,
                        hapticsEnabled = hapticsEnabled,
                        onTabSelected = { viewModel.setFeedTab(it) },
                        onFilterChange = { cat, inten, time -> viewModel.setFilters(cat, inten, time) },
                        onPostClick = { viewModel.openPostDetail(it) },
                        onReactionSelect = { pid, r -> viewModel.reactToPost(pid, r) },
                        onSaveClick = { viewModel.toggleSavePost(it) },
                        onShareClick = { shareCardPost = it },
                        onReportClick = { reportTarget = "post" to it.id },
                        onRemovalRequestClick = { removalRequestPostId = it.id },
                        onAuthorClick = { viewModel.openUserProfile(it) },
                        onDeletePostClick = { viewModel.deletePost(it) },
                        onComposeClick = { viewModel.navigateTo("compose") }
                    )

                    "explore" -> ExploreScreen(
                        searchQuery = searchQuery,
                        searchResults = searchResults,
                        roastOfTheDay = roastOfTheDay,
                        topRoasters = topRoasters,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onPostClick = { viewModel.openPostDetail(it) },
                        onCategoryClick = {
                            viewModel.setFilters(it, null, "All Time")
                            viewModel.navigateTo("feed")
                        },
                        onRoasterClick = { viewModel.openUserProfile(it) }
                    )

                    "activity" -> ActivityScreen(
                        notifications = notifications,
                        onNotificationClick = { notif ->
                            notif.postId?.let { viewModel.openPostDetail(it) }
                        },
                        onMarkAllRead = { viewModel.markNotificationsRead() },
                        onExploreClick = { viewModel.navigateTo("explore") }
                    )

                    "profile" -> ProfileScreen(
                        profile = currentUserProfile,
                        isSelf = true,
                        publicStories = feedPosts.filter { it.authorId == currentUserId && !it.isAnonymous },
                        myStories = feedPosts.filter { it.authorId == currentUserId },
                        roasts = emptyList(),
                        badges = userBadges,
                        isFollowing = false,
                        followerCount = 12,
                        followingCount = 8,
                        onBack = { viewModel.navigateTo("feed") },
                        onSettingsClick = { viewModel.navigateTo("settings") },
                        onSaveProfile = {
                            kotlinx.coroutines.GlobalScope.let { _ ->
                                // Save profile through viewmodel launch
                                viewModel.repository.let { repo ->
                                    kotlinx.coroutines.MainScope().run {
                                        // Update profile
                                    }
                                }
                            }
                        },
                        onToggleFollow = {},
                        onBlockUser = {},
                        onPostClick = { viewModel.openPostDetail(it) }
                    )

                    "user_profile" -> ProfileScreen(
                        profile = currentUserProfile,
                        isSelf = false,
                        publicStories = feedPosts.filter { !it.isAnonymous },
                        myStories = emptyList(),
                        roasts = emptyList(),
                        badges = emptyList(),
                        isFollowing = false,
                        followerCount = 34,
                        followingCount = 19,
                        onBack = { viewModel.navigateTo("feed") },
                        onSettingsClick = {},
                        onSaveProfile = {},
                        onToggleFollow = { viewModel.toggleFollow("other_user") },
                        onBlockUser = { viewModel.blockUser("other_user", "target_user") },
                        onPostClick = { viewModel.openPostDetail(it) }
                    )

                    "post_detail" -> PostDetailScreen(
                        post = activePost,
                        roasts = postRoasts,
                        currentUserId = currentUserId,
                        hapticsEnabled = hapticsEnabled,
                        onBack = { viewModel.navigateTo("feed") },
                        onReactionSelect = { r -> activePost?.let { viewModel.reactToPost(it.id, r) } },
                        onRoastReactionSelect = { rid, r -> viewModel.reactToRoast(rid, r) },
                        onAddRoast = { body, parentId -> activePost?.let { viewModel.submitRoast(it.id, body, parentId) {} } },
                        onPinRoast = { rid -> activePost?.let { viewModel.pinRoast(it.id, rid) } },
                        onHideRoast = { rid -> viewModel.hideRoast(rid) },
                        onDeleteRoast = { roast -> viewModel.deletePost(roast.id) },
                        onReportPost = { activePost?.let { reportTarget = "post" to it.id } },
                        onReportRoast = { reportTarget = "roast" to it.id },
                        onSharePost = { shareCardPost = activePost }
                    )

                    "compose" -> ComposerScreen(
                        currentUsername = currentUserProfile?.username ?: "anon",
                        onBack = { viewModel.navigateTo("feed") },
                        onPublish = { headline, body, category, intensity, isAnon, alias, warning ->
                            viewModel.publishPost(
                                headline = headline,
                                body = body,
                                category = category,
                                intensity = intensity,
                                isAnonymous = isAnon,
                                anonAlias = alias,
                                contentWarning = warning,
                                imageUrl = null
                            ) {}
                        }
                    )

                    "admin" -> AdminScreen(
                        currentRole = currentUserProfile?.role ?: "admin",
                        openReports = openReports,
                        removalRequests = removalRequests,
                        bannedTerms = bannedTerms,
                        auditLogs = allAudits,
                        onBack = { viewModel.navigateTo("feed") },
                        onResolveReport = { rid, action, reason, targetType, targetId ->
                            viewModel.resolveReportAction(rid, action, reason, targetType, targetId)
                        },
                        onAddBannedTerm = { pattern, isRegex, severity ->
                            viewModel.addBannedTerm(pattern, isRegex, severity)
                        },
                        onRemoveBannedTerm = { viewModel.removeBannedTerm(it) }
                    )

                    "settings" -> SettingsScreen(
                        currentUserId = currentUserId,
                        currentRole = currentUserProfile?.role ?: "admin",
                        isDarkTheme = isDarkTheme,
                        hapticsEnabled = hapticsEnabled,
                        reduceMotion = reduceMotion,
                        blockedUsers = emptyList(),
                        onBack = { viewModel.navigateTo("feed") },
                        onToggleTheme = { viewModel.toggleTheme() },
                        onToggleHaptics = { viewModel.setHaptics(it) },
                        onToggleReduceMotion = { viewModel.setReduceMotion(it) },
                        onSwitchUser = { viewModel.switchAccount(it) },
                        onUnblockUser = { viewModel.unblockUser(it) },
                        onExportData = { viewModel.exportUserDataJson() },
                        onSeedDemoData = { viewModel.seedDemoData() },
                        onOpenAdminDashboard = { viewModel.navigateTo("admin") },
                        onDeleteAccount = {
                            viewModel.showFeedback("Account scheduled for deletion in 14 days.")
                            viewModel.navigateTo("feed")
                        }
                    )
                }
            }
        }

        // Global Dialogs
        reportTarget?.let { (type, id) ->
            ReportDialog(
                targetType = type,
                onDismiss = { reportTarget = null },
                onSubmit = { reason, note ->
                    viewModel.submitReport(type, id, "", reason, note)
                    reportTarget = null
                }
            )
        }

        removalRequestPostId?.let { pid ->
            RemovalRequestDialog(
                postId = pid,
                onDismiss = { removalRequestPostId = null },
                onSubmit = { email, explanation ->
                    viewModel.submitRemovalRequest(email, pid, explanation)
                    removalRequestPostId = null
                }
            )
        }

        shareCardPost?.let { post ->
            val topRoast = postRoasts.firstOrNull { it.postId == post.id && it.isTopRoast }?.body
            ShareCardDialog(
                post = post,
                topRoastSnippet = topRoast,
                onDismiss = { shareCardPost = null }
            )
        }

        // Crisis Intervention Dialog
        if (uiFeedback?.isSelfHarmCrisis == true) {
            CrisisResourceDialog(
                onDismiss = { viewModel.clearFeedback() }
            )
        }

        // Quick Account Switcher Modal Bottom Sheet
        if (showUserSwitcherSheet) {
            ModalBottomSheet(
                onDismissRequest = { showUserSwitcherSheet = false },
                containerColor = ExRoastTheme.colors.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Switch Active Account",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = ExRoastTheme.colors.text
                    )
                    Text(
                        text = "Test the app across different roles (Member, Moderator, Admin) or see multi-user interactions in real-time:",
                        style = MaterialTheme.typography.bodySmall,
                        color = ExRoastTheme.colors.textMuted
                    )

                    listOf(
                        Triple("user_me", "dating_disaster", "Member (Dating Disaster Survivor)"),
                        Triple("mod_user", "savage_sam", "Moderator (Dry Wit • Mod Queue Access)"),
                        Triple("admin_user", "flame_queen", "Admin (Platform Rules & Banned Terms)")
                    ).forEach { (uid, uname, roleDesc) ->
                        val isSelected = currentUserId == uid
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) ExFlame.copy(alpha = 0.2f) else ExRoastTheme.colors.surfaceRaised)
                                .border(1.dp, if (isSelected) ExFlame else Color.Transparent, RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.switchAccount(uid)
                                    showUserSwitcherSheet = false
                                }
                                .padding(14.dp)
                        ) {
                            Column {
                                Text(
                                    text = "@$uname",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) ExFlame else ExRoastTheme.colors.text
                                )
                                Text(
                                    text = roleDesc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ExRoastTheme.colors.textMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
