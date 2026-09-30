package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PostEntity
import com.example.ui.theme.ExDanger
import com.example.ui.theme.ExDarkBorder
import com.example.ui.theme.ExFlame
import com.example.ui.theme.ExFlameHot
import com.example.ui.theme.ExGold
import com.example.ui.theme.ExRoastTheme
import com.example.ui.theme.StoryTextStyle

@Composable
fun StoryCard(
    post: PostEntity,
    currentUserId: String,
    isSaved: Boolean,
    userReaction: String?,
    hapticsEnabled: Boolean,
    onCardClick: () -> Unit,
    onReactionSelect: (String) -> Unit,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    onReportClick: () -> Unit,
    onRemovalRequestClick: () -> Unit,
    onAuthorClick: () -> Unit,
    onDeletePostClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    var isWarningRevealed by remember { mutableStateOf(post.contentWarning.isNullOrBlank()) }
    var isExpanded by remember { mutableStateOf(false) }

    val isOwnPost = post.authorId == currentUserId

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("story_card_${post.id}")
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = ExRoastTheme.colors.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(ExRoastTheme.colors.border)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Author info, Category & Intensity, and Overflow Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Author representation
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clickable { if (!post.isAnonymous) onAuthorClick() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (post.isAnonymous) ExRoastTheme.colors.surfaceRaised
                                else ExFlame.copy(alpha = 0.2f)
                            )
                            .border(1.dp, ExRoastTheme.colors.border, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (post.isAnonymous) "🎭" else "🔥",
                            fontSize = 18.sp
                        )
                    }

                    Column {
                        Text(
                            text = if (post.isAnonymous) post.anonAlias else "@${post.authorId}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = ExRoastTheme.colors.text,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = formatRelativeTime(post.createdAt),
                            style = MaterialTheme.typography.labelSmall,
                            color = ExRoastTheme.colors.textMuted
                        )
                    }
                }

                // Tags & Menu
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Category Chip
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

                    // Intensity Flame Badge
                    IntensityBadge(post.intensity)

                    // Overflow Menu
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options",
                                tint = ExRoastTheme.colors.textMuted
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Report story") },
                                onClick = {
                                    showMenu = false
                                    onReportClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("This is about me (Removal)") },
                                onClick = {
                                    showMenu = false
                                    onRemovalRequestClick()
                                }
                            )
                            if (isOwnPost) {
                                DropdownMenuItem(
                                    text = { Text("Delete Story", color = ExDanger) },
                                    onClick = {
                                        showMenu = false
                                        onDeletePostClick()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Headline
            Text(
                text = post.headline,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = ExRoastTheme.colors.text,
                lineHeight = 24.sp
            )

            // Content Warning reveal banner
            if (!post.contentWarning.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ExFlame.copy(alpha = 0.15f))
                        .border(1.dp, ExFlame.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .clickable { isWarningRevealed = !isWarningRevealed }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = ExFlame,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Content Warning: ${post.contentWarning}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = ExFlame
                            )
                        }
                        Icon(
                            imageVector = if (isWarningRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Reveal",
                            tint = ExFlame,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Story Body (Collapsed if content warning not yet revealed)
            AnimatedVisibility(visible = isWarningRevealed) {
                Column {
                    val previewLength = 260
                    val needsTruncation = post.body.length > previewLength && !isExpanded

                    Text(
                        text = if (needsTruncation) post.body.take(previewLength) + "..." else post.body,
                        style = StoryTextStyle,
                        color = ExRoastTheme.colors.text
                    )

                    if (post.body.length > previewLength) {
                        Text(
                            text = if (isExpanded) "Show less" else "Read full story",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = ExFlame,
                            modifier = Modifier
                                .clickable { isExpanded = !isExpanded }
                                .padding(top = 4.dp)
                        )
                    }
                }
            }

            // Bottom Actions Bar: Reactions, Roast Count, Save, Share
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reactions
                ReactionRow(
                    totalCount = post.reactionCount,
                    activeReaction = userReaction,
                    hapticsEnabled = hapticsEnabled,
                    onSelectReaction = onReactionSelect
                )

                // Comments, Save & Share
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Roasts count
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onCardClick() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Comment,
                            contentDescription = "Roasts",
                            tint = ExRoastTheme.colors.textMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${post.roastCount}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = ExRoastTheme.colors.textMuted
                        )
                    }

                    // Save
                    IconButton(
                        onClick = onSaveClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Story",
                            tint = if (isSaved) ExGold else ExRoastTheme.colors.textMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Share
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = ExRoastTheme.colors.textMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IntensityBadge(intensity: String) {
    val (flameText, color) = when (intensity) {
        "Extra Crispy" -> "🔥 Extra Crispy" to ExFlame
        "Medium" -> "🔥 Medium" to ExFlameHot
        else -> "🔥 Mild" to ExGold
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(
            text = flameText,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}

fun formatRelativeTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days < 7 -> "${days}d ago"
        else -> "${days / 7}w ago"
    }
}
