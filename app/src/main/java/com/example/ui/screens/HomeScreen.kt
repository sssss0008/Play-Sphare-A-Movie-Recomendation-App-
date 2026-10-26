package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameDto
import com.example.ui.components.CategorySelectorRow
import com.example.ui.components.FeaturedHeroCard
import com.example.ui.components.GameCard
import com.example.ui.components.MysteryRouletteDialog
import com.example.ui.components.PlatformSelectorRow
import com.example.ui.components.PopularHorizontalGameCard
import com.example.ui.components.SortBottomSheet
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CoralPink
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onGameClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var showSortSheet by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_games_list"),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Header: App Logo, Tagline & Mystery Dice Button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NeonPurple.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SportsEsports,
                                    contentDescription = "PlaySphere Logo",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "PlaySphere",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "400+ Free-to-Play Games",
                                fontSize = 11.sp,
                                color = CyberCyan,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Mystery Game Roulette button
                    Surface(
                        onClick = { viewModel.rollMysteryGame() },
                        shape = RoundedCornerShape(12.dp),
                        color = NeonPurple.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple),
                        modifier = Modifier
                            .height(40.dp)
                            .testTag("mystery_picker_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Casino,
                                contentDescription = "Surprise Me",
                                tint = NeonPurple,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Surprise Me",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            // Search Bar & Sort Button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        placeholder = { Text("Search games, genres, developers...", color = TextMuted, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = if (state.searchQuery.isNotBlank()) CyberCyan else TextMuted
                            )
                        },
                        trailingIcon = {
                            if (state.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear search",
                                        tint = TextMuted
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceDark,
                            unfocusedContainerColor = SurfaceDark,
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = BorderDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("search_games_input")
                    )

                    Surface(
                        onClick = { showSortSheet = true },
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("sort_filter_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = "Sort games",
                                tint = CyberCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // Platform Filter Selector (All, PC, Browser)
            item {
                Spacer(modifier = Modifier.height(6.dp))
                PlatformSelectorRow(
                    platforms = viewModel.platforms,
                    selectedPlatform = state.selectedPlatform,
                    onPlatformSelected = { viewModel.onPlatformSelected(it) }
                )
            }

            // Category Chips Row
            item {
                Spacer(modifier = Modifier.height(10.dp))
                CategorySelectorRow(
                    categories = viewModel.categories,
                    selectedCategory = state.selectedCategory,
                    onCategorySelected = { viewModel.onCategorySelected(it) }
                )
            }

            // Loading State
            if (state.isLoading) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = CyberCyan,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Loading free game database...",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else if (state.errorMessage != null) {
                // Error State
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Unable to connect",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoralPink
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = state.errorMessage ?: "Please check your network connection.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadGames(isRefresh = true) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF090D16))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Retry", color = Color(0xFF090D16), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // If not searching, show Spotlight Hero & Trending Row
                if (state.searchQuery.isBlank()) {
                    state.featuredGame?.let { featured ->
                        item {
                            Spacer(modifier = Modifier.height(14.dp))
                            FeaturedHeroCard(
                                game = featured,
                                onClick = { onGameClick(featured.id) },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }

                    if (state.popularGames.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(18.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🔥 Most Popular Titles",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${state.allGames.size} Games Total",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.popularGames) { game ->
                                    PopularHorizontalGameCard(
                                        game = game,
                                        onClick = { onGameClick(game.id) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Main Games Section Header
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (state.searchQuery.isNotBlank()) "Search Results (${state.filteredGames.size})"
                            else "${state.selectedCategory} Games (${state.filteredGames.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Empty Results State
                if (state.filteredGames.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🎮 No Games Found",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Try modifying your search query or selecting a different category.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    // Games List
                    items(state.filteredGames, key = { it.id }) { game ->
                        GameCard(
                            game = game,
                            isSaved = state.vaultIds.contains(game.id),
                            onClick = { onGameClick(game.id) },
                            onToggleVault = { viewModel.toggleVault(game) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Sort Bottom Sheet
        if (showSortSheet) {
            SortBottomSheet(
                sortOptions = viewModel.sortOptions,
                selectedSort = state.selectedSort,
                onSortSelected = { viewModel.onSortSelected(it) },
                onDismiss = { showSortSheet = false }
            )
        }

        // Mystery Roulette Dialog
        if (state.isRollingMystery || state.mysteryGame != null) {
            MysteryRouletteDialog(
                game = state.mysteryGame,
                isRolling = state.isRollingMystery,
                onOpenGame = { game ->
                    viewModel.dismissMysteryGame()
                    onGameClick(game.id)
                },
                onReroll = { viewModel.rollMysteryGame() },
                onDismiss = { viewModel.dismissMysteryGame() }
            )
        }
    }
}
