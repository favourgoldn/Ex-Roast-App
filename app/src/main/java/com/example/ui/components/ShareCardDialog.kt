package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PostEntity
import com.example.ui.theme.ExFlame
import com.example.ui.theme.ExGold
import com.example.ui.theme.ExRoastTheme

@Composable
fun ShareCardDialog(
    post: PostEntity,
    topRoastSnippet: String?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val shareText = buildString {
        append("🔥 EX ROAST: ${post.headline}\n\n")
        append("\"${post.body.take(200)}...\"\n\n")
        if (!topRoastSnippet.isNullOrBlank()) {
            append("👑 Top Roast: \"$topRoastSnippet\"\n\n")
        }
        append("Turn your worst dating disasters into your best stories on EX ROAST.")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ExRoastTheme.colors.surface,
        title = {
            Text(
                text = "Share Story",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = ExRoastTheme.colors.text
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Branded Story Card Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0B0B0F))
                        .border(1.dp, ExFlame.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                BrokenHeartFlameIcon(size = 20.dp)
                                Text(
                                    text = "EX ROAST",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = post.category.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ExFlame
                            )
                        }

                        Text(
                            text = post.headline,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 22.sp
                        )

                        Text(
                            text = post.body.take(140) + "...",
                            fontSize = 13.sp,
                            color = Color(0xFFD0D0DE),
                            lineHeight = 18.sp
                        )

                        if (!topRoastSnippet.isNullOrBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ExGold.copy(alpha = 0.1f))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "👑 \"$topRoastSnippet\"",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ExGold
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share EX ROAST story"))
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ExFlame),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Text("Share Card", modifier = Modifier.padding(start = 6.dp))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = ExRoastTheme.colors.textMuted)
            }
        }
    )
}
