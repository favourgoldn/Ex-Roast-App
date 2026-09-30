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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PostEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.ui.theme.ExFlame
import com.example.ui.theme.ExGold
import com.example.ui.theme.ExRoastTheme

val TRENDING_TAGS = listOf(
    "#PayPalGuac",
    "#FingerpickingGhost",
    "#MamaChickenParm",
    "#TextingCrime",
    "#RedFlagCity",
    "#ExAuditing"
)

val CATEGORY_TILES = listOf(
    "First Dates" to "🍸",
    "Situationships" to "🌀",
    "Texting Crimes" to "📱",
    "Cheaters" to "🕵️",
    "Meet the Parents" to "👵",
    "Breakup Fails" to "💔",
    "Gifts Gone Wrong" to "🎁",
    "Social Media Stalking" to "👀",
    "Red Flags" to "🚩",
    "Redemption Arcs" to "✨"
)

@Composable
fun ExploreScreen(
    searchQuery: String,
    searchResults: List<PostEntity>,
    roastOfTheDay: PostEntity?,
    topRoasters: List<UserProfileEntity>,
    onSearchChange: (String) -> Unit,
    onPostClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onRoasterClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ExRoastTheme.colors.bg)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search stories, categories, or keywords...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = ExRoastTheme.colors.textMuted)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = ExRoastTheme.colors.textMuted)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = ExRoastTheme.colors.surface,
                unfocusedContainerColor = ExRoastTheme.colors.surface,
                focusedBorderColor = ExFlame,
                unfocusedBorderColor = ExRoastTheme.colors.border
            ),
            singleLine = true
        )

        if (searchQuery.isNotBlank()) {
            // Search Results
            Text(
                text = "SEARCH RESULTS (${searchResults.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = ExRoastTheme.colors.text
            )

            if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No dating disasters found matching \"$searchQuery\". Try checking the categories below.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ExRoastTheme.colors.textMuted
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(searchResults, key = { it.id }) { post ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPostClick(post.id) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = ExRoastTheme.colors.surface),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(ExRoastTheme.colors.border)
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = post.category.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = ExFlame
                                )
                                Text(
                                    text = post.headline,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ExRoastTheme.colors.text
                                )
                                Text(
                                    text = post.body.take(120) + "...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ExRoastTheme.colors.textMuted
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Explore Hub
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Roast of the Day
                if (roastOfTheDay != null) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Whatshot, contentDescription = null, tint = ExGold, modifier = Modifier.size(20.dp))
                                Text(
                                    text = "ROAST OF THE DAY",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                    color = ExGold,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onPostClick(roastOfTheDay.id) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = ExRoastTheme.colors.surface),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(ExGold.copy(alpha = 0.6f))
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = roastOfTheDay.category.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = ExFlame
                                    )
                                    Text(
                                        text = roastOfTheDay.headline,
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = ExRoastTheme.colors.text
                                    )
                                    Text(
                                        text = roastOfTheDay.body.take(160) + "...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = ExRoastTheme.colors.textMuted
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "🔥 ${roastOfTheDay.reactionCount} reactions",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = ExFlame
                                        )
                                        Text(
                                            text = "💬 ${roastOfTheDay.roastCount} roasts",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = ExRoastTheme.colors.textMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Trending Hashtags
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "TRENDING TAGS",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = ExRoastTheme.colors.text
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(TRENDING_TAGS) { tag ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(ExRoastTheme.colors.surfaceRaised)
                                        .clickable { onSearchChange(tag.removePrefix("#")) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = tag,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = ExFlame
                                    )
                                }
                            }
                        }
                    }
                }

                // Top Roasters Leaderboard Preview
                if (topRoasters.isNotEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "TOP ROASTERS THIS WEEK",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = ExRoastTheme.colors.text
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(topRoasters) { roaster ->
                                    Card(
                                        modifier = Modifier
                                            .width(130.dp)
                                            .clickable { onRoasterClick(roaster.userId) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = ExRoastTheme.colors.surfaceRaised)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(CircleShape)
                                                    .background(ExFlame.copy(alpha = 0.2f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("🔥", fontSize = 20.sp)
                                            }
                                            Text(
                                                text = "@${roaster.username}",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = ExRoastTheme.colors.text,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${roaster.roastScore} pts",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                                color = ExGold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Browse Categories Grid
                item {
                    Text(
                        text = "BROWSE DISASTERS BY CATEGORY",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = ExRoastTheme.colors.text
                    )
                }

                items(CATEGORY_TILES.chunked(2)) { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pair.forEach { (catName, emoji) ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onCategoryClick(catName) },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = ExRoastTheme.colors.surface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(text = emoji, fontSize = 22.sp)
                                    Text(
                                        text = catName,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = ExRoastTheme.colors.text
                                    )
                                }
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
