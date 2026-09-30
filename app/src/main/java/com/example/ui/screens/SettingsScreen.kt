package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BlockMuteEntity
import com.example.ui.theme.ExDanger
import com.example.ui.theme.ExFlame
import com.example.ui.theme.ExGold
import com.example.ui.theme.ExRoastTheme

@Composable
fun SettingsScreen(
    currentUserId: String,
    currentRole: String,
    isDarkTheme: Boolean,
    hapticsEnabled: Boolean,
    reduceMotion: Boolean,
    blockedUsers: List<BlockMuteEntity>,
    onBack: () -> Unit,
    onToggleTheme: () -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onToggleReduceMotion: (Boolean) -> Unit,
    onSwitchUser: (String) -> Unit,
    onUnblockUser: (String) -> Unit,
    onExportData: () -> String,
    onSeedDemoData: () -> Unit,
    onOpenAdminDashboard: () -> Unit,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var legalDialogContent by remember { mutableStateOf<Pair<String, String>?>(null) }
    var showDeleteAccountConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ExRoastTheme.colors.bg)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ExRoastTheme.colors.text)
            }
            Text(
                text = "SETTINGS & SAFETY",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = ExRoastTheme.colors.text
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Admin HQ Shortcut
            if (currentRole in listOf("admin", "moderator")) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenAdminDashboard() },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = ExFlame.copy(alpha = 0.15f)),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(ExFlame.copy(alpha = 0.5f))
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = ExFlame, modifier = Modifier.size(24.dp))
                            Column {
                                Text("Admin & Moderator Dashboard", fontWeight = FontWeight.Bold, color = ExFlame)
                                Text("Review open reports, removal requests & banned terms", fontSize = 12.sp, color = ExRoastTheme.colors.textMuted)
                            }
                        }
                    }
                }
            }

            // Role / Test Account Switcher
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "TEST ACCOUNT SWITCHER",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = ExRoastTheme.colors.textMuted
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = ExRoastTheme.colors.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Instantly switch user identities to test roasts, reports, and admin moderation:",
                                fontSize = 12.sp,
                                color = ExRoastTheme.colors.textMuted
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    "admin_user" to "Admin (flame_queen)",
                                    "mod_user" to "Mod (savage_sam)",
                                    "user_me" to "Member (me)"
                                ).forEach { (uid, label) ->
                                    val isCurrent = currentUserId == uid
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isCurrent) ExFlame else ExRoastTheme.colors.surfaceRaised)
                                            .clickable { onSwitchUser(uid) }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCurrent) Color.White else ExRoastTheme.colors.text
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Developer Seeding Tool
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "DEVELOPMENT SEEDING",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = ExRoastTheme.colors.textMuted
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = ExRoastTheme.colors.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Per specification, EX ROAST launches empty in production. Use this button to populate demo stories, roasters, and crowns:",
                                fontSize = 12.sp,
                                color = ExRoastTheme.colors.textMuted
                            )
                            Button(
                                onClick = onSeedDemoData,
                                colors = ButtonDefaults.buttonColors(containerColor = ExRoastTheme.colors.surfaceRaised),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Load Demo Community Stories", color = ExFlame, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Appearance & Haptics
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "PREFERENCES",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = ExRoastTheme.colors.textMuted
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = ExRoastTheme.colors.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            // Dark Theme Switch
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Icon(Icons.Default.Brightness4, contentDescription = null, tint = ExRoastTheme.colors.text)
                                    Text("Dark Theme", style = MaterialTheme.typography.bodyMedium, color = ExRoastTheme.colors.text)
                                }
                                Switch(checked = isDarkTheme, onCheckedChange = { onToggleTheme() }, colors = SwitchDefaults.colors(checkedThumbColor = ExFlame))
                            }

                            // Haptics Switch
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Icon(Icons.Default.Vibration, contentDescription = null, tint = ExRoastTheme.colors.text)
                                    Text("Reaction Haptic Feedback", style = MaterialTheme.typography.bodyMedium, color = ExRoastTheme.colors.text)
                                }
                                Switch(checked = hapticsEnabled, onCheckedChange = onToggleHaptics, colors = SwitchDefaults.colors(checkedThumbColor = ExFlame))
                            }
                        }
                    }
                }
            }

            // Data Rights (GDPR / NDPR Export)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "DATA & PRIVACY RIGHTS",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = ExRoastTheme.colors.textMuted
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = ExRoastTheme.colors.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Download your full user profile, stories, roasts, and score history as JSON.",
                                fontSize = 12.sp,
                                color = ExRoastTheme.colors.textMuted
                            )
                            Button(
                                onClick = {
                                    val jsonData = onExportData()
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, jsonData)
                                        type = "application/json"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Export EX ROAST Data Archive"))
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ExRoastTheme.colors.surfaceRaised),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp), tint = ExRoastTheme.colors.text)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Download My Data (JSON)", color = ExRoastTheme.colors.text, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Legal & Community Rules
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "LEGAL & RULES",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = ExRoastTheme.colors.textMuted
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = ExRoastTheme.colors.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Community Rules",
                                fontWeight = FontWeight.Bold,
                                color = ExFlame,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        legalDialogContent = "Community Rules" to """
                                            1. Roasts punch at stories and behavior, never at bodies or protected traits.
                                            2. Never post real identifiable details (names, numbers, handles, addresses).
                                            3. Screenshots must have all names and faces hidden.
                                            4. No revenge porn or sexual content.
                                            5. Respect takedown requests ("This is about me").
                                        """.trimIndent()
                                    }
                                    .padding(vertical = 4.dp)
                            )
                            Text(
                                text = "Terms of Service",
                                fontWeight = FontWeight.Bold,
                                color = ExRoastTheme.colors.text,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        legalDialogContent = "Terms of Service" to """
                                            By using EX ROAST, you confirm you are 18 or older.
                                            You are solely responsible for content you share.
                                            Stories must be true-to-experience and non-identifying.
                                            Violations result in warnings, suspensions, or permanent account bans.
                                        """.trimIndent()
                                    }
                                    .padding(vertical = 4.dp)
                            )
                            Text(
                                text = "Privacy Policy & DMCA Takedown",
                                fontWeight = FontWeight.Bold,
                                color = ExRoastTheme.colors.text,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        legalDialogContent = "Privacy Policy & DMCA" to """
                                            EX ROAST prioritizes anonymity and privacy.
                                            Anonymous authors are never exposed to public queries.
                                            If you believe a story infringes copyright or identifies you, submit a removal request via the 'This is about me' flow.
                                        """.trimIndent()
                                    }
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Delete Account
            item {
                Button(
                    onClick = { showDeleteAccountConfirm = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ExDanger.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = ExDanger, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delete Account", color = ExDanger, fontWeight = FontWeight.Bold)
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Legal Document Dialog
    legalDialogContent?.let { (title, body) ->
        AlertDialog(
            onDismissRequest = { legalDialogContent = null },
            containerColor = ExRoastTheme.colors.surface,
            title = { Text(title, fontWeight = FontWeight.Bold, color = ExRoastTheme.colors.text) },
            text = { Text(body, color = ExRoastTheme.colors.text, lineHeight = 20.sp) },
            confirmButton = {
                TextButton(onClick = { legalDialogContent = null }) {
                    Text("Close", color = ExFlame)
                }
            }
        )
    }

    // Delete Account Confirmation Dialog
    if (showDeleteAccountConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountConfirm = false },
            containerColor = ExRoastTheme.colors.surface,
            title = { Text("Delete Account?", fontWeight = FontWeight.Bold, color = ExDanger) },
            text = {
                Text(
                    "Your account will enter a 14-day grace period during which all personal information is queued for permanent purge per GDPR requirements.",
                    color = ExRoastTheme.colors.text
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteAccount()
                        showDeleteAccountConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExDanger)
                ) {
                    Text("Confirm Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountConfirm = false }) {
                    Text("Cancel", color = ExRoastTheme.colors.textMuted)
                }
            }
        )
    }
}
