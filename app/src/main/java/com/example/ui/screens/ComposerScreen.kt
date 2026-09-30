package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.safety.SafetyChecker
import com.example.ui.theme.ExFlame
import com.example.ui.theme.ExFlameHot
import com.example.ui.theme.ExGold
import com.example.ui.theme.ExRoastTheme

val COMPOSER_CATEGORIES = listOf(
    "First Dates",
    "Situationships",
    "Texting Crimes",
    "Cheaters",
    "Meet the Parents",
    "Breakup Fails",
    "Gifts Gone Wrong",
    "Social Media Stalking",
    "Red Flags",
    "Redemption Arcs"
)

@Composable
fun ComposerScreen(
    currentUsername: String,
    onBack: () -> Unit,
    onPublish: (
        headline: String,
        body: String,
        category: String,
        intensity: String,
        isAnonymous: Boolean,
        anonAlias: String,
        contentWarning: String?
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var headline by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(COMPOSER_CATEGORIES.first()) }
    var intensity by remember { mutableStateOf("Medium") }
    var isAnonymous by remember { mutableStateOf(true) }
    var anonAlias by remember { mutableStateOf("Anonymous Roaster") }
    var hasContentWarning by remember { mutableStateOf(false) }
    var contentWarningText by remember { mutableStateOf("") }

    // Live Anonymization Helper: detects capitalized words that could be real names
    val potentialNames = remember(body) {
        if (body.length > 20) SafetyChecker.findPotentialNames(body) else emptyList()
    }

    val isHeadlineValid = headline.trim().length in 10..120
    val isBodyValid = body.trim().length in 50..3000
    val canPublish = isHeadlineValid && isBodyValid

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
                    .padding(horizontal = 8.dp, vertical = 6.dp),
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
                    text = "Spill the Story",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ExRoastTheme.colors.text
                )

                Button(
                    onClick = {
                        if (canPublish) {
                            onPublish(
                                headline.trim(),
                                body.trim(),
                                category,
                                intensity,
                                isAnonymous,
                                anonAlias.trim(),
                                if (hasContentWarning) contentWarningText.trim() else null
                            )
                        }
                    },
                    enabled = canPublish,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ExFlame,
                        disabledContainerColor = ExRoastTheme.colors.surfaceRaised
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Spill it",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Posting mode toggle (Anonymous vs as @username)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ExRoastTheme.colors.surface)
                    .border(1.dp, ExRoastTheme.colors.border, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isAnonymous) "Posting as Anonymous Roaster" else "Posting as @$currentUsername",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = ExRoastTheme.colors.text
                        )
                        Text(
                            text = if (isAnonymous) "Your profile and identity are 100% hidden from everyone." else "Attributed to your public profile.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ExRoastTheme.colors.textMuted
                        )
                    }

                    Switch(
                        checked = isAnonymous,
                        onCheckedChange = { isAnonymous = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = ExFlame, checkedTrackColor = ExFlame.copy(alpha = 0.4f))
                    )
                }
            }

            // Nickname Customization (if Anonymous)
            if (isAnonymous) {
                OutlinedTextField(
                    value = anonAlias,
                    onValueChange = { anonAlias = it },
                    label = { Text("Anonymous Nickname (Optional)") },
                    placeholder = { Text("e.g. The Sock Thief, The Ghost King") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
            }

            // Category Selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = ExRoastTheme.colors.text
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(COMPOSER_CATEGORIES) { cat ->
                        val selected = category == cat
                        FilterChip(
                            selected = selected,
                            onClick = { category = cat },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ExFlame,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Roast Intensity Dial
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Roast Intensity Dial",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = ExRoastTheme.colors.text
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Mild", "Medium", "Extra Crispy").forEach { inten ->
                        val selected = intensity == inten
                        FilterChip(
                            selected = selected,
                            onClick = { intensity = inten },
                            label = { Text("🔥 $inten") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (inten == "Extra Crispy") ExFlame else ExFlameHot,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Headline Field
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Headline",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = ExRoastTheme.colors.text
                    )
                    Text(
                        text = "${headline.length} / 120",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (headline.length in 10..120) ExRoastTheme.colors.textMuted else ExFlame
                    )
                }

                OutlinedTextField(
                    value = headline,
                    onValueChange = { headline = it },
                    placeholder = { Text("The one-sentence hook (10-120 chars)...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    isError = headline.isNotEmpty() && !isHeadlineValid,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ExFlame,
                        unfocusedBorderColor = ExRoastTheme.colors.border,
                        focusedContainerColor = ExRoastTheme.colors.surface,
                        unfocusedContainerColor = ExRoastTheme.colors.surface
                    ),
                    maxLines = 2
                )
            }

            // Story Body Field
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "The Story",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = ExRoastTheme.colors.text
                    )
                    Text(
                        text = "${body.length} / 3000 (min 50)",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (body.length in 50..3000) ExRoastTheme.colors.textMuted else ExFlame
                    )
                }

                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    placeholder = { Text("Give the internet the unabridged dating disaster. What happened, what was said, and how it imploded...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 6,
                    maxLines = 14,
                    isError = body.isNotEmpty() && !isBodyValid,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ExFlame,
                        unfocusedBorderColor = ExRoastTheme.colors.border,
                        focusedContainerColor = ExRoastTheme.colors.surface,
                        unfocusedContainerColor = ExRoastTheme.colors.surface
                    )
                )
            }

            // Ex Anonymization Nudge & Name Highlighter
            if (potentialNames.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ExGold.copy(alpha = 0.1f))
                        .border(1.dp, ExGold.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = ExGold, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Ex Anonymization Nudge",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = ExGold
                            )
                        }
                        Text(
                            text = "Are these real names: ${potentialNames.take(4).joinToString(", ")}? Call them 'The Guac Auditor' or 'The Drummer' instead to protect their privacy and stay compliant.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ExRoastTheme.colors.text
                        )
                    }
                }
            }

            // Content Warnings Switch
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ExRoastTheme.colors.surface)
                    .border(1.dp, ExRoastTheme.colors.border, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Add Content Warning",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = ExRoastTheme.colors.text
                        )
                        Switch(
                            checked = hasContentWarning,
                            onCheckedChange = { hasContentWarning = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = ExFlame)
                        )
                    }

                    if (hasContentWarning) {
                        OutlinedTextField(
                            value = contentWarningText,
                            onValueChange = { contentWarningText = it },
                            placeholder = { Text("e.g. mentions cheating, loud screaming, cringe...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                    }
                }
            }

            // Safety rules reminder note
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = ExRoastTheme.colors.textMuted,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Pre-publish check will verify no phone numbers, emails, addresses, social handles, links, or banned harassment terms are present.",
                    style = MaterialTheme.typography.labelSmall,
                    color = ExRoastTheme.colors.textMuted
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
