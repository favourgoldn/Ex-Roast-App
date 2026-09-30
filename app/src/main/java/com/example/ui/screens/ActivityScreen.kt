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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.NotificationEntity
import com.example.ui.components.formatRelativeTime
import com.example.ui.theme.ExFlame
import com.example.ui.theme.ExGold
import com.example.ui.theme.ExRoastTheme

@Composable
fun ActivityScreen(
    notifications: List<NotificationEntity>,
    onNotificationClick: (NotificationEntity) -> Unit,
    onMarkAllRead: () -> Unit,
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ExRoastTheme.colors.bg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ACTIVITY",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp
                ),
                color = ExRoastTheme.colors.text
            )

            if (notifications.any { !it.isRead }) {
                TextButton(onClick = onMarkAllRead) {
                    Icon(Icons.Default.DoneAll, contentDescription = null, tint = ExFlame, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Mark all read",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = ExFlame,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("🦗", fontSize = 48.sp)
                    Text(
                        text = "Quiet in here.",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = ExRoastTheme.colors.text
                    )
                    Text(
                        text = "No one has roasted you yet. Go roast somebody or spill your own disaster to start the fire.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ExRoastTheme.colors.textMuted,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = onExploreClick,
                        colors = ButtonDefaults.buttonColors(containerColor = ExFlame),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Find Someone to Roast", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications, key = { it.id }) { notif ->
                    val (iconEmoji, iconBg) = when (notif.type) {
                        "roast" -> "🔥" to ExFlame.copy(alpha = 0.2f)
                        "comeback" -> "💀" to ExRoastTheme.colors.surfaceRaised
                        "top_roast" -> "👑" to ExGold.copy(alpha = 0.2f)
                        "badge" -> "🏆" to ExGold.copy(alpha = 0.2f)
                        "follower" -> "👤" to ExRoastTheme.colors.surfaceRaised
                        else -> "⚡" to ExRoastTheme.colors.surfaceRaised
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNotificationClick(notif) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (!notif.isRead) ExRoastTheme.colors.surfaceRaised else ExRoastTheme.colors.surface
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(
                                if (!notif.isRead) ExFlame.copy(alpha = 0.5f) else ExRoastTheme.colors.border
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(iconBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(iconEmoji, fontSize = 20.sp)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = notif.message,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (!notif.isRead) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = ExRoastTheme.colors.text
                                )
                                Text(
                                    text = formatRelativeTime(notif.createdAt),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ExRoastTheme.colors.textMuted
                                )
                            }

                            if (!notif.isRead) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(ExFlame)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
