package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.GameVaultEntity
import com.example.ui.components.AddToVaultDialog
import com.example.ui.components.PlatformBadge
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CoralPink
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.VaultViewModel

@Composable
fun VaultScreen(
    viewModel: VaultViewModel,
    onGameClick: (Int) -> Unit,
    onNavigateExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var editingGame by remember { mutableStateOf<GameVaultEntity?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("vault_screen_list"),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "My Game Vault",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Track backlogs, played titles, and offline notes",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            // Stats Cards Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBox(
                        title = "Total",
                        count = state.stats.total,
                        color = CyberCyan,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        title = "Playing",
                        count = state.stats.playing,
                        color = NeonPurple,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        title = "Completed",
                        count = state.stats.completed,
                        color = AmberGold,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        title = "Backlog",
                        count = state.stats.wantToPlay,
                        color = CoralPink,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Search Bar in Vault
            item {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search your vault...", color = TextMuted, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (state.searchQuery.isNotBlank()) CyberCyan else TextMuted
                        )
                    },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
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
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(50.dp)
                        .testTag("vault_search_input")
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Status Filter Chips
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.statuses) { status ->
                        val isSelected = status == state.selectedStatus
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setStatusFilter(status) },
                            label = {
                                Text(
                                    text = status,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = SurfaceDark,
                                labelColor = TextSecondary,
                                selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                                selectedLabelColor = CyberCyan
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = BorderDark,
                                selectedBorderColor = CyberCyan
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("vault_status_chip_$status")
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Empty State
            if (state.displayedGames.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = NeonPurple.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple.copy(alpha = 0.3f)),
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.VideogameAsset,
                                    contentDescription = null,
                                    tint = NeonPurple,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (state.allGames.isEmpty()) "Your Vault is Empty" else "No Matching Games Found",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (state.allGames.isEmpty())
                                "Discover 400+ free games and bookmark your favorites or track what you're playing!"
                            else "No games match the selected status or search query.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        if (state.allGames.isEmpty()) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = onNavigateExplore,
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("discover_games_button")
                            ) {
                                Text("Discover Free Games", color = Color(0xFF090D16), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // List of Saved Games
                items(state.displayedGames, key = { it.id }) { game ->
                    VaultGameCard(
                        game = game,
                        onClick = { onGameClick(game.id) },
                        onEdit = { editingGame = game },
                        onDelete = { viewModel.removeGame(game.id) },
                        onPlay = { openGameUrl(context, game.gameUrl) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Edit Dialog
        editingGame?.let { game ->
            AddToVaultDialog(
                initialStatus = game.status,
                initialRating = game.rating,
                initialNotes = game.personalNotes,
                isAlreadySaved = true,
                onSave = { status, rating, notes ->
                    viewModel.updateStatus(game.copy(status = status, rating = rating, personalNotes = notes), status)
                    editingGame = null
                },
                onDelete = {
                    viewModel.removeGame(game.id)
                    editingGame = null
                },
                onDismiss = { editingGame = null }
            )
        }
    }
}

@Composable
private fun StatBox(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 10.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun VaultGameCard(
    game: GameVaultEntity,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("vault_card_${game.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(70.dp)
            ) {
                AsyncImage(
                    model = game.thumbnail,
                    contentDescription = game.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = NeonPurple.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = game.status,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonPurple,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }

                    if (game.rating > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${game.rating}/5",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberGold
                            )
                        }
                    }
                }

                if (game.personalNotes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "“${game.personalNotes}”",
                        fontSize = 11.sp,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Quick actions
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit notes",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = onPlay,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInBrowser,
                        contentDescription = "Play",
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

private fun openGameUrl(context: Context, url: String?) {
    if (url.isNullOrBlank()) {
        Toast.makeText(context, "No URL found", Toast.LENGTH_SHORT).show()
        return
    }
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to open link", Toast.LENGTH_SHORT).show()
    }
}
