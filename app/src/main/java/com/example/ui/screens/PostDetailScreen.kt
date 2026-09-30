package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PostEntity
import com.example.data.local.entity.RoastEntity
import com.example.ui.components.IntensityBadge
import com.example.ui.components.ReactionRow
import com.example.ui.components.RoastItem
import com.example.ui.components.formatRelativeTime
import com.example.ui.theme.ExFlame
import com.example.ui.theme.ExGold
import com.example.ui.theme.ExRoastTheme
import com.example.ui.theme.StoryTextStyle

@Composable
fun PostDetailScreen(
    post: PostEntity?,
    roasts: List<RoastEntity>,
    currentUserId: String,
    hapticsEnabled: Boolean,
    onBack: () -> Unit,
    onReactionSelect: (reaction: String) -> Unit,
    onRoastReactionSelect: (roastId: String, reaction: String) -> Unit,
    onAddRoast: (body: String, parentRoastId: String?) -> Unit,
    onPinRoast: (roastId: String) -> Unit,
    onHideRoast: (roastId: String) -> Unit,
    onDeleteRoast: (RoastEntity) -> Unit,
    onReportPost: () -> Unit,
    onReportRoast: (RoastEntity) -> Unit,
    onSharePost: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (post == null) {
        Box(
            modifier = modifier.fillMaxSize().background(ExRoastTheme.colors.bg),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = ExFlame)
        }
        return
    }

    var roastInput by remember { mutableStateOf("") }
    var replyingToRoast by remember { mutableStateOf<RoastEntity?>(null) }
    var sortOrder by remember { mutableStateOf("top") } // top, newest, oldest

    val sortedRoasts = remember(roasts, sortOrder) {
        when (sortOrder) {
            "newest" -> roasts.sortedByDescending { it.createdAt }
            "oldest" -> roasts.sortedBy { it.createdAt }
            else -> roasts.sortedWith(compareByDescending<RoastEntity> { it.isPinned }
                .thenByDescending { it.isTopRoast }
                .thenByDescending { it.reactionCount }
                .thenByDescending { it.createdAt })
        }
    }

    val isPostAuthor = post.authorId == currentUserId

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        containerColor = ExRoastTheme.colors.bg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ExRoastTheme.colors.text
                    )
                }

                Text(
                    text = "Story & Roasts",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ExRoastTheme.colors.text
                )

                IconButton(onClick = onSharePost) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = ExRoastTheme.colors.text
                    )
                }
            }
        },
        bottomBar = {
            // Sticky Roast Composer
            if (post.roastsMode == "off") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ExRoastTheme.colors.surfaceRaised)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = ExRoastTheme.colors.textMuted, modifier = Modifier.size(16.dp))
                        Text(
                            text = "Roasts have been disabled by the author.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ExRoastTheme.colors.textMuted
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ExRoastTheme.colors.surface)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    // Replying indicator
                    if (replyingToRoast != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Replying to @${replyingToRoast?.authorName} (Comeback)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = ExFlame
                            )
                            IconButton(
                                onClick = { replyingToRoast = null },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Cancel reply", tint = ExRoastTheme.colors.textMuted, modifier = Modifier.size(14.dp))
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = roastInput,
                            onValueChange = { roastInput = it },
                            placeholder = {
                                Text(
                                    text = if (replyingToRoast != null) "Drop your comeback..." else "Say something unforgettable...",
                                    fontSize = 14.sp
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ExFlame,
                                unfocusedBorderColor = ExRoastTheme.colors.border,
                                focusedContainerColor = ExRoastTheme.colors.surfaceRaised,
                                unfocusedContainerColor = ExRoastTheme.colors.surfaceRaised
                            ),
                            maxLines = 3
                        )

                        IconButton(
                            onClick = {
                                if (roastInput.isNotBlank()) {
                                    onAddRoast(roastInput.trim(), replyingToRoast?.id)
                                    roastInput = ""
                                    replyingToRoast = null
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (roastInput.isNotBlank()) ExFlame else ExRoastTheme.colors.surfaceRaised)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Submit Roast",
                                tint = if (roastInput.isNotBlank()) Color.White else ExRoastTheme.colors.textMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Story Detail Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ExRoastTheme.colors.surface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(ExRoastTheme.colors.border)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(ExFlame.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(if (post.isAnonymous) "🎭" else "🔥", fontSize = 18.sp)
                                }
                                Column {
                                    Text(
                                        text = if (post.isAnonymous) post.anonAlias else "@${post.authorId}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = ExRoastTheme.colors.text
                                    )
                                    Text(
                                        text = formatRelativeTime(post.createdAt),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ExRoastTheme.colors.textMuted
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ExRoastTheme.colors.surfaceRaised)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = post.category,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                        color = ExRoastTheme.colors.accentHot
                                    )
                                }
                                IntensityBadge(post.intensity)
                            }
                        }

                        // Headline
                        Text(
                            text = post.headline,
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                            color = ExRoastTheme.colors.text
                        )

                        // Body
                        Text(
                            text = post.body,
                            style = StoryTextStyle,
                            color = ExRoastTheme.colors.text
                        )

                        // Reactions on Post
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ReactionRow(
                                totalCount = post.reactionCount,
                                activeReaction = null,
                                hapticsEnabled = hapticsEnabled,
                                onSelectReaction = onReactionSelect
                            )

                            Text(
                                text = "${roasts.size} Roasts",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = ExRoastTheme.colors.textMuted
                            )
                        }
                    }
                }
            }

            // Roasts Header & Sort Controls
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "THE ROAST PIT (${roasts.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = ExRoastTheme.colors.text,
                        letterSpacing = 0.5.sp
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("top" to "Top", "newest" to "New", "oldest" to "Old").forEach { (key, label) ->
                            val isSelected = sortOrder == key
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) ExFlame else ExRoastTheme.colors.surfaceRaised)
                                    .clickable { sortOrder = key }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else ExRoastTheme.colors.textMuted
                                )
                            }
                        }
                    }
                }
            }

            // Roast Pit Empty State
            if (sortedRoasts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("💀", fontSize = 36.sp)
                            Text(
                                text = "No roasts yet",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = ExRoastTheme.colors.text
                            )
                            Text(
                                text = "Step into the pit. Be the first to roast this story below.",
                                style = MaterialTheme.typography.bodySmall,
                                color = ExRoastTheme.colors.textMuted
                            )
                        }
                    }
                }
            } else {
                items(sortedRoasts, key = { it.id }) { roast ->
                    RoastItem(
                        roast = roast,
                        currentUserId = currentUserId,
                        isPostAuthor = isPostAuthor,
                        userReaction = null,
                        hapticsEnabled = hapticsEnabled,
                        onReactionSelect = { r -> onRoastReactionSelect(roast.id, r) },
                        onReplyClick = { replyingToRoast = roast },
                        onPinClick = { onPinRoast(roast.id) },
                        onHideClick = { onHideRoast(roast.id) },
                        onDeleteClick = { onDeleteRoast(roast) },
                        onReportClick = { onReportRoast(roast) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
