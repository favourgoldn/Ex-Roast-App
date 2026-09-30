package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.data.local.entity.PostEntity
import com.example.ui.components.StoryCard
import com.example.ui.theme.ExFlame
import com.example.ui.theme.ExFlameHot
import com.example.ui.theme.ExRoastTheme
import com.example.ui.viewmodel.FeedTab
import com.example.ui.viewmodel.FilterState

val CATEGORIES = listOf(
    "All",
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

val INTENSITIES = listOf("All", "Mild", "Medium", "Extra Crispy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    posts: List<PostEntity>,
    currentTab: FeedTab,
    filterState: FilterState,
    currentUserId: String,
    savedPostIds: List<String>,
    hapticsEnabled: Boolean,
    onTabSelected: (FeedTab) -> Unit,
    onFilterChange: (category: String?, intensity: String?, timeRange: String) -> Unit,
    onPostClick: (String) -> Unit,
    onReactionSelect: (postId: String, reaction: String) -> Unit,
    onSaveClick: (String) -> Unit,
    onShareClick: (PostEntity) -> Unit,
    onReportClick: (PostEntity) -> Unit,
    onRemovalRequestClick: (PostEntity) -> Unit,
    onAuthorClick: (String) -> Unit,
    onDeletePostClick: (String) -> Unit,
    onComposeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ExRoastTheme.colors.bg)
    ) {
        // Tab Row: For You, Hot, New, Following
        val tabs = listOf(
            FeedTab.FOR_YOU to "For You",
            FeedTab.HOT to "🔥 Hot",
            FeedTab.NEW to "New",
            FeedTab.FOLLOWING to "Following"
        )
        val selectedIndex = tabs.indexOfFirst { it.first == currentTab }.coerceAtLeast(0)

        TabRow(
            selectedTabIndex = selectedIndex,
            containerColor = ExRoastTheme.colors.bg,
            contentColor = ExFlame,
            indicator = { tabPositions ->
                if (selectedIndex < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                        color = ExFlame,
                        height = 3.dp
                    )
                }
            },
            divider = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(ExRoastTheme.colors.border)
                )
            }
        ) {
            tabs.forEachIndexed { index, (tab, title) ->
                Tab(
                    selected = index == selectedIndex,
                    onClick = { onTabSelected(tab) },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (index == selectedIndex) ExRoastTheme.colors.text else ExRoastTheme.colors.textMuted
                        )
                    }
                )
            }
        }

        // Quick Category Chip Carousel & Filter Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { showFilterSheet = true },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter Stories",
                    tint = if (filterState.category != null || filterState.intensity != null) ExFlame else ExRoastTheme.colors.textMuted
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(CATEGORIES) { cat ->
                    val isSelected = (cat == "All" && filterState.category == null) || (filterState.category == cat)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) ExFlame
                                else ExRoastTheme.colors.surfaceRaised
                            )
                            .clickable {
                                onFilterChange(
                                    if (cat == "All") null else cat,
                                    filterState.intensity,
                                    filterState.timeRange
                                )
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = if (isSelected) Color.White else ExRoastTheme.colors.text
                        )
                    }
                }
            }
        }

        // Posts List or Empty State
        if (posts.isEmpty()) {
            EmptyFeedState(
                currentTab = currentTab,
                onComposeClick = onComposeClick
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(posts, key = { it.id }) { post ->
                    StoryCard(
                        post = post,
                        currentUserId = currentUserId,
                        isSaved = savedPostIds.contains(post.id),
                        userReaction = null,
                        hapticsEnabled = hapticsEnabled,
                        onCardClick = { onPostClick(post.id) },
                        onReactionSelect = { r -> onReactionSelect(post.id, r) },
                        onSaveClick = { onSaveClick(post.id) },
                        onShareClick = { onShareClick(post) },
                        onReportClick = { onReportClick(post) },
                        onRemovalRequestClick = { onRemovalRequestClick(post) },
                        onAuthorClick = { onAuthorClick(post.authorId) },
                        onDeletePostClick = { onDeletePostClick(post.id) }
                    )
                }
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilterSheet) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            containerColor = ExRoastTheme.colors.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Filter Feed",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = ExRoastTheme.colors.text
                )

                // Intensity Filter
                Text(
                    text = "Roast Intensity",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = ExRoastTheme.colors.text
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    INTENSITIES.forEach { inten ->
                        val selected = (inten == "All" && filterState.intensity == null) || (filterState.intensity == inten)
                        FilterChip(
                            selected = selected,
                            onClick = {
                                onFilterChange(
                                    filterState.category,
                                    if (inten == "All") null else inten,
                                    filterState.timeRange
                                )
                            },
                            label = { Text(inten) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ExFlame,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Time Range
                Text(
                    text = "Time Range",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = ExRoastTheme.colors.text
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All Time", "Today", "Week", "Month").forEach { time ->
                        FilterChip(
                            selected = filterState.timeRange == time,
                            onClick = {
                                onFilterChange(
                                    filterState.category,
                                    filterState.intensity,
                                    time
                                )
                            },
                            label = { Text(time) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ExFlameHot,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Button(
                    onClick = { showFilterSheet = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ExFlame),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Apply Filters")
                }
            }
        }
    }
}

@Composable
fun EmptyFeedState(
    currentTab: FeedTab,
    onComposeClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🔥",
                fontSize = 48.sp
            )
            Text(
                text = when (currentTab) {
                    FeedTab.FOLLOWING -> "No followed stories yet"
                    else -> "No stories yet"
                },
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = ExRoastTheme.colors.text
            )
            Text(
                text = when (currentTab) {
                    FeedTab.FOLLOWING -> "You aren't following anyone with public stories yet. Explore other roasters to build your feed."
                    else -> "Someone out there has a really bad ex. Be the first."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = ExRoastTheme.colors.textMuted,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onComposeClick,
                colors = ButtonDefaults.buttonColors(containerColor = ExFlame),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Spill it",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
