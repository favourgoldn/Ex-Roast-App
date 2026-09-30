package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BadgeEntity
import com.example.data.local.entity.PostEntity
import com.example.data.local.entity.RoastEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.ui.components.StoryCard
import com.example.ui.theme.ExFlame
import com.example.ui.theme.ExFlameHot
import com.example.ui.theme.ExGold
import com.example.ui.theme.ExRoastTheme

@Composable
fun ProfileScreen(
    profile: UserProfileEntity?,
    isSelf: Boolean,
    publicStories: List<PostEntity>,
    myStories: List<PostEntity>,
    roasts: List<RoastEntity>,
    badges: List<BadgeEntity>,
    isFollowing: Boolean,
    followerCount: Int,
    followingCount: Int,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onSaveProfile: (UserProfileEntity) -> Unit,
    onToggleFollow: () -> Unit,
    onBlockUser: () -> Unit,
    onPostClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (profile == null) {
        Box(modifier = modifier.fillMaxSize().background(ExRoastTheme.colors.bg), contentAlignment = Alignment.Center) {
            Text("User not found", color = ExRoastTheme.colors.textMuted)
        }
        return
    }

    var selectedTabIndex by remember { mutableStateOf(0) }
    var showEditDialog by remember { mutableStateOf(false) }

    val tabs = if (isSelf) {
        listOf("Stories", "My Pit (All)", "Roasts", "Badges")
    } else {
        listOf("Stories", "Roasts", "Badges")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ExRoastTheme.colors.bg)
    ) {
        // Top Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isSelf) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ExRoastTheme.colors.text)
                }
            } else {
                Spacer(modifier = Modifier.width(40.dp))
            }

            Text(
                text = "@${profile.username}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = ExRoastTheme.colors.text
            )

            if (isSelf) {
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = ExRoastTheme.colors.text)
                }
            } else {
                IconButton(onClick = onBlockUser) {
                    Icon(Icons.Default.Block, contentDescription = "Block", tint = ExRoastTheme.colors.textMuted)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Header
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar with Roast Score ring
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(ExRoastTheme.colors.surfaceRaised)
                                .border(3.dp, ExFlame, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔥", fontSize = 36.sp)
                        }

                        // Stats
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${profile.roastScore}",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                    color = ExGold
                                )
                                Text("Score", style = MaterialTheme.typography.labelSmall, color = ExRoastTheme.colors.textMuted)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$followerCount",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                    color = ExRoastTheme.colors.text
                                )
                                Text("Followers", style = MaterialTheme.typography.labelSmall, color = ExRoastTheme.colors.textMuted)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$followingCount",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                    color = ExRoastTheme.colors.text
                                )
                                Text("Following", style = MaterialTheme.typography.labelSmall, color = ExRoastTheme.colors.textMuted)
                            }
                        }
                    }

                    // Display Name, Level & Streak
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = profile.displayName.ifBlank { profile.username },
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = ExRoastTheme.colors.text
                            )
                            // Level Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ExFlame.copy(alpha = 0.15f))
                                    .border(1.dp, ExFlame.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = profile.level,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ExFlame
                                )
                            }
                            // Streak Flame
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = ExFlameHot, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "${profile.currentStreak}d",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ExFlameHot
                                )
                            }
                        }

                        // Bio
                        if (profile.bio.isNotBlank()) {
                            Text(
                                text = profile.bio,
                                style = MaterialTheme.typography.bodyMedium,
                                color = ExRoastTheme.colors.text
                            )
                        }

                        // Roast Style Chip
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ExRoastTheme.colors.surfaceRaised)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Style: ${profile.roastStyle}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ExRoastTheme.colors.accentHot
                                )
                            }
                        }
                    }

                    // Action Button (Edit profile or Follow/Unfollow)
                    if (isSelf) {
                        Button(
                            onClick = { showEditDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ExRoastTheme.colors.surfaceRaised),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = ExRoastTheme.colors.text)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Profile", color = ExRoastTheme.colors.text, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = onToggleFollow,
                            colors = ButtonDefaults.buttonColors(containerColor = if (isFollowing) ExRoastTheme.colors.surfaceRaised else ExFlame),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isFollowing) "Following" else "Follow",
                                color = if (isFollowing) ExRoastTheme.colors.text else Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Tab Row
            item {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = ExRoastTheme.colors.bg,
                    contentColor = ExFlame,
                    indicator = { tabPositions ->
                        if (selectedTabIndex < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = ExFlame,
                                height = 2.dp
                            )
                        }
                    }
                ) {
                    tabs.forEachIndexed { index, tabTitle ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = tabTitle,
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = if (selectedTabIndex == index) ExRoastTheme.colors.text else ExRoastTheme.colors.textMuted
                                )
                            }
                        )
                    }
                }
            }

            // Tab Content
            when (tabs[selectedTabIndex]) {
                "Stories" -> {
                    if (publicStories.isEmpty()) {
                        item {
                            Text("No public stories shared yet.", color = ExRoastTheme.colors.textMuted, modifier = Modifier.padding(20.dp))
                        }
                    } else {
                        items(publicStories, key = { it.id }) { post ->
                            StoryCard(
                                post = post,
                                currentUserId = profile.userId,
                                isSaved = false,
                                userReaction = null,
                                hapticsEnabled = true,
                                onCardClick = { onPostClick(post.id) },
                                onReactionSelect = {},
                                onSaveClick = {},
                                onShareClick = {},
                                onReportClick = {},
                                onRemovalRequestClick = {},
                                onAuthorClick = {},
                                onDeletePostClick = {}
                            )
                        }
                    }
                }
                "My Pit (All)" -> {
                    if (myStories.isEmpty()) {
                        item {
                            Text("You haven't posted any stories yet.", color = ExRoastTheme.colors.textMuted, modifier = Modifier.padding(20.dp))
                        }
                    } else {
                        items(myStories, key = { it.id }) { post ->
                            StoryCard(
                                post = post,
                                currentUserId = profile.userId,
                                isSaved = false,
                                userReaction = null,
                                hapticsEnabled = true,
                                onCardClick = { onPostClick(post.id) },
                                onReactionSelect = {},
                                onSaveClick = {},
                                onShareClick = {},
                                onReportClick = {},
                                onRemovalRequestClick = {},
                                onAuthorClick = {},
                                onDeletePostClick = {}
                            )
                        }
                    }
                }
                "Roasts" -> {
                    if (roasts.isEmpty()) {
                        item {
                            Text("No roasts delivered yet.", color = ExRoastTheme.colors.textMuted, modifier = Modifier.padding(20.dp))
                        }
                    } else {
                        items(roasts, key = { it.id }) { roast ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onPostClick(roast.postId) },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = ExRoastTheme.colors.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = roast.body,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = ExRoastTheme.colors.text
                                    )
                                    Text(
                                        text = "🔥 ${roast.reactionCount} reactions",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = ExFlame
                                    )
                                }
                            }
                        }
                    }
                }
                "Badges" -> {
                    if (badges.isEmpty()) {
                        item {
                            Text("No badges unlocked yet. Start posting and roasting to unlock status.", color = ExRoastTheme.colors.textMuted, modifier = Modifier.padding(20.dp))
                        }
                    } else {
                        items(badges, key = { it.id }) { badge ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = ExRoastTheme.colors.surface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(ExGold.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🏆", fontSize = 22.sp)
                                    }
                                    Column {
                                        Text(
                                            text = badge.title,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = ExGold
                                        )
                                        Text(
                                            text = badge.description,
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
    }

    // Edit Profile Dialog
    if (showEditDialog) {
        var editDisplayName by remember { mutableStateOf(profile.displayName) }
        var editBio by remember { mutableStateOf(profile.bio) }
        var editRoastStyle by remember { mutableStateOf(profile.roastStyle) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = ExRoastTheme.colors.surface,
            title = {
                Text("Edit Profile", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = ExRoastTheme.colors.text)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editDisplayName,
                        onValueChange = { editDisplayName = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { if (it.length <= 160) editBio = it },
                        label = { Text("Bio (${editBio.length}/160)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 3
                    )
                    Text("Roast Style", style = MaterialTheme.typography.labelMedium, color = ExRoastTheme.colors.text)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Savage", "Sarcastic", "Wholesome Burn", "Dry Wit").forEach { style ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (editRoastStyle == style) ExFlame else ExRoastTheme.colors.surfaceRaised)
                                    .clickable { editRoastStyle = style }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(style, fontSize = 11.sp, color = if (editRoastStyle == style) Color.White else ExRoastTheme.colors.text)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveProfile(
                            profile.copy(
                                displayName = editDisplayName.trim(),
                                bio = editBio.trim(),
                                roastStyle = editRoastStyle
                            )
                        )
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExFlame),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = ExRoastTheme.colors.textMuted)
                }
            }
        )
    }
}
