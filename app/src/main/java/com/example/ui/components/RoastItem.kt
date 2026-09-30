package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.RoastEntity
import com.example.ui.theme.ExDanger
import com.example.ui.theme.ExDarkBorder
import com.example.ui.theme.ExFlame
import com.example.ui.theme.ExGold
import com.example.ui.theme.ExRoastTheme

@Composable
fun RoastItem(
    roast: RoastEntity,
    currentUserId: String,
    isPostAuthor: Boolean,
    userReaction: String?,
    hapticsEnabled: Boolean,
    onReactionSelect: (String) -> Unit,
    onReplyClick: () -> Unit,
    onPinClick: () -> Unit,
    onHideClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onReportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOwnRoast = roast.authorId == currentUserId

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (roast.isTopRoast) ExGold.copy(alpha = 0.08f) else ExRoastTheme.colors.surfaceRaised
        ),
        border = if (roast.isTopRoast) CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(ExGold.copy(alpha = 0.5f))
        ) else CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(ExRoastTheme.colors.border)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Badges row: Top Roast Crown or Pinned by Author
            if (roast.isTopRoast || roast.isPinned) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (roast.isTopRoast) {
                        Text(
                            text = "👑 TOP ROAST",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                            color = ExGold
                        )
                    }
                    if (roast.isPinned) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = null,
                                tint = ExFlame,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Pinned by author",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = ExFlame
                            )
                        }
                    }
                }
            }

            // Author & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(ExFlame.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "💀", fontSize = 12.sp)
                    }
                    Text(
                        text = "@${roast.authorName}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = ExRoastTheme.colors.text
                    )
                    Text(
                        text = "• ${formatRelativeTime(roast.createdAt)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = ExRoastTheme.colors.textMuted
                    )
                }

                // Quick Action Icons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (isPostAuthor) {
                        IconButton(
                            onClick = onPinClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "Pin Roast",
                                tint = if (roast.isPinned) ExFlame else ExRoastTheme.colors.textMuted,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        IconButton(
                            onClick = onHideClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VisibilityOff,
                                contentDescription = "Hide Roast",
                                tint = ExRoastTheme.colors.textMuted,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    if (isOwnRoast) {
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = ExDanger,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = onReportClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = "Report",
                                tint = ExRoastTheme.colors.textMuted,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Roast Text
            Text(
                text = roast.body,
                style = MaterialTheme.typography.bodyMedium,
                color = ExRoastTheme.colors.text,
                lineHeight = 20.sp
            )

            // Reactions and Reply Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ReactionRow(
                    totalCount = roast.reactionCount,
                    activeReaction = userReaction,
                    hapticsEnabled = hapticsEnabled,
                    onSelectReaction = onReactionSelect
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onReplyClick() }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Reply,
                        contentDescription = "Comeback",
                        tint = ExFlame,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (roast.replyCount > 0) "${roast.replyCount} Comebacks" else "Comeback",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ExFlame
                    )
                }
            }
        }
    }
}
