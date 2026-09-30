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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BannedTermEntity
import com.example.data.local.entity.ModerationAuditEntity
import com.example.data.local.entity.RemovalRequestEntity
import com.example.data.local.entity.ReportEntity
import com.example.ui.components.formatRelativeTime
import com.example.ui.theme.ExDanger
import com.example.ui.theme.ExFlame
import com.example.ui.theme.ExGold
import com.example.ui.theme.ExInfo
import com.example.ui.theme.ExRoastTheme

@Composable
fun AdminScreen(
    currentRole: String,
    openReports: List<ReportEntity>,
    removalRequests: List<RemovalRequestEntity>,
    bannedTerms: List<BannedTermEntity>,
    auditLogs: List<ModerationAuditEntity>,
    onBack: () -> Unit,
    onResolveReport: (reportId: String, action: String, reason: String, targetType: String, targetId: String) -> Unit,
    onAddBannedTerm: (pattern: String, isRegex: Boolean, severity: String) -> Unit,
    onRemoveBannedTerm: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Queue (${openReports.size})", "Removal (${removalRequests.size})", "Banned Terms", "Audit Log")

    var showAddTermDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ExRoastTheme.colors.bg)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ExRoastTheme.colors.text)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = ExFlame)
                Text(
                    text = "MODERATION & AUDIT HQ",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = ExRoastTheme.colors.text
                )
            }
        }

        // Tab Selector
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = ExRoastTheme.colors.bg,
            contentColor = ExFlame,
            indicator = { tabPositions ->
                if (selectedTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = ExFlame,
                        height = 2.dp
                    )
                }
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedTab == index) ExRoastTheme.colors.text else ExRoastTheme.colors.textMuted
                        )
                    }
                )
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // Reports Queue
                if (openReports.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("Queue clean! No open reports pending review.", color = ExRoastTheme.colors.textMuted)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(openReports, key = { it.id }) { report ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = ExRoastTheme.colors.surface),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(
                                        if (report.priority == "high") ExDanger.copy(alpha = 0.5f) else ExRoastTheme.colors.border
                                    )
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Reason: ${report.reason}",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (report.priority == "high") ExDanger else ExFlame
                                        )
                                        Text(
                                            text = report.priority.uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                            color = if (report.priority == "high") ExDanger else ExGold
                                        )
                                    }

                                    if (report.note.isNotBlank()) {
                                        Text(
                                            text = "Reporter note: \"${report.note}\"",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = ExRoastTheme.colors.textMuted
                                        )
                                    }

                                    // Action buttons
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                onResolveReport(report.id, "remove_content", report.reason, report.targetType, report.targetId)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = ExDanger),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Remove Content", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                onResolveReport(report.id, "warn_user", "Warning issued", report.targetType, report.targetId)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = ExRoastTheme.colors.surfaceRaised),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Warn", fontSize = 11.sp, color = ExRoastTheme.colors.text)
                                        }

                                        Button(
                                            onClick = {
                                                onResolveReport(report.id, "dismiss", "No violation found", report.targetType, report.targetId)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = ExRoastTheme.colors.surfaceRaised),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Dismiss", fontSize = 11.sp, color = ExRoastTheme.colors.textMuted)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // Removal Requests ("This is about me")
                if (removalRequests.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No active 'This is about me' removal requests.", color = ExRoastTheme.colors.textMuted)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(removalRequests, key = { it.id }) { req ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = ExRoastTheme.colors.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "Requester: ${req.requesterEmail}",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = ExRoastTheme.colors.text
                                    )
                                    Text(
                                        text = "Claim: \"${req.explanation}\"",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = ExRoastTheme.colors.text
                                    )
                                    Text(
                                        text = "Status: ${req.status}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = ExFlame
                                    )
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // Banned Terms Manager
                Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ACTIVE BANNED TERMS (${bannedTerms.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = ExRoastTheme.colors.text
                        )
                        Button(
                            onClick = { showAddTermDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ExFlame),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Add Pattern", fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                        }
                    }

                    if (bannedTerms.isEmpty()) {
                        Text("No custom banned terms configured. Default safety rules apply.", color = ExRoastTheme.colors.textMuted)
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(bannedTerms, key = { it.id }) { term ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = ExRoastTheme.colors.surface)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(term.pattern, fontWeight = FontWeight.Bold, color = ExRoastTheme.colors.text)
                                            Text(if (term.isRegex) "Regex • ${term.severity}" else "Keyword • ${term.severity}", fontSize = 11.sp, color = ExRoastTheme.colors.textMuted)
                                        }
                                        IconButton(onClick = { onRemoveBannedTerm(term.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ExDanger, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            3 -> {
                // Immutable Audit Log
                if (auditLogs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No moderation actions recorded yet.", color = ExRoastTheme.colors.textMuted)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(auditLogs, key = { it.id }) { audit ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = ExRoastTheme.colors.surface)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "@${audit.moderatorName} -> ${audit.action.uppercase()}",
                                            fontWeight = FontWeight.Bold,
                                            color = ExFlame,
                                            fontSize = 13.sp
                                        )
                                        Text(formatRelativeTime(audit.createdAt), fontSize = 11.sp, color = ExRoastTheme.colors.textMuted)
                                    }
                                    Text(
                                        text = "Reason: ${audit.reason} (Target: ${audit.targetType})",
                                        fontSize = 12.sp,
                                        color = ExRoastTheme.colors.text
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddTermDialog) {
        var pattern by remember { mutableStateOf("") }
        var isRegex by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddTermDialog = false },
            containerColor = ExRoastTheme.colors.surface,
            title = { Text("Add Banned Term", color = ExRoastTheme.colors.text) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = pattern,
                        onValueChange = { pattern = it },
                        label = { Text("Word or Regex pattern") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pattern.isNotBlank()) {
                            onAddBannedTerm(pattern.trim(), isRegex, "block")
                            showAddTermDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExFlame)
                ) {
                    Text("Add Term")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTermDialog = false }) {
                    Text("Cancel", color = ExRoastTheme.colors.textMuted)
                }
            }
        )
    }
}
