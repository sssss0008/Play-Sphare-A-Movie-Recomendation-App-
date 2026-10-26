package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.GameDetailDto
import com.example.data.model.MinimumSystemRequirements
import com.example.ui.components.AddToVaultDialog
import com.example.ui.components.FullscreenScreenshotModal
import com.example.ui.components.PlatformBadge
import com.example.ui.theme.AmberGold
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BadgeLive
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
import com.example.ui.viewmodel.GameDetailViewModel

@Composable
fun GameDetailScreen(
    viewModel: GameDetailViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var isDescriptionExpanded by remember { mutableStateOf(false) }

    BackHandler {
        onBack()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        if (state.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = CyberCyan)
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Loading game specifications...",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        } else if (state.errorMessage != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Error Loading Game Details",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = CoralPink
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = state.errorMessage ?: "",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.loadDetail() },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF090D16))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Retry", color = Color(0xFF090D16), fontWeight = FontWeight.Bold)
                }
            }
        } else {
            state.game?.let { game ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("game_detail_content"),
                    contentPadding = PaddingValues(bottom = 60.dp)
                ) {
                    // Hero Image with Overlaid Back & Share Buttons
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                        ) {
                            AsyncImage(
                                model = game.thumbnail,
                                contentDescription = game.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Gradient Scrim
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0x90090D16),
                                                Color.Transparent,
                                                BackgroundDark
                                            )
                                        )
                                    )
                            )

                            // Top Action Bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    onClick = onBack,
                                    shape = CircleShape,
                                    color = Color(0xAA090D16),
                                    modifier = Modifier
                                        .size(42.dp)
                                        .testTag("detail_back_button")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back",
                                            tint = Color.White
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Surface(
                                        onClick = {
                                            shareGame(context, game)
                                        },
                                        shape = CircleShape,
                                        color = Color(0xAA090D16),
                                        modifier = Modifier
                                            .size(42.dp)
                                            .testTag("detail_share_button")
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = "Share",
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Bottom Chips over hero
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = BadgeLive.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BadgeLive)
                                ) {
                                    Text(
                                        text = (game.status ?: "LIVE").uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BadgeLive,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Surface(
                                    color = CyberCyan.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan)
                                ) {
                                    Text(
                                        text = game.genre ?: "Free Game",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberCyan,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                PlatformBadge(platform = game.platform ?: "")
                            }
                        }
                    }

                    // Title & Quick Actions
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = game.title,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "By ${game.developer ?: game.publisher ?: "Unknown Studio"}",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action Buttons (Play Game & Vault)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Play / Get Free Button
                                Button(
                                    onClick = {
                                        openGameUrl(context, game.gameUrl ?: game.profileUrl)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .height(48.dp)
                                        .testTag("play_game_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.OpenInBrowser,
                                        contentDescription = null,
                                        tint = Color(0xFF090D16),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Play Game",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF090D16)
                                    )
                                }

                                // Backlog / Vault Button
                                val isInVault = state.isInVault
                                Button(
                                    onClick = { viewModel.showVaultDialog(true) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isInVault) NeonPurple.copy(alpha = 0.25f) else SurfaceElevatedDark
                                    ),
                                    border = if (isInVault) androidx.compose.foundation.BorderStroke(1.dp, NeonPurple) else null,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("vault_action_button")
                                ) {
                                    Icon(
                                        imageVector = if (isInVault) Icons.Default.CheckCircle else Icons.Default.BookmarkAdd,
                                        contentDescription = null,
                                        tint = if (isInVault) NeonPurple else TextPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isInVault) (state.vaultEntity?.status ?: "In Vault") else "Add Vault",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isInVault) NeonPurple else TextPrimary,
                                        maxLines = 1
                                    )
                                }
                            }

                            // Saved status / notes preview if in vault
                            if (state.isInVault && state.vaultEntity != null) {
                                val entity = state.vaultEntity!!
                                Spacer(modifier = Modifier.height(10.dp))
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = "Saved in Vault: ${entity.status}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = CyberCyan
                                            )
                                            if (entity.rating > 0) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(top = 2.dp)
                                                ) {
                                                    for (i in 1..entity.rating) {
                                                        Icon(
                                                            imageVector = Icons.Default.Star,
                                                            contentDescription = null,
                                                            tint = AmberGold,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        Text(
                                            text = "Edit Notes",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextSecondary,
                                            modifier = Modifier.clickable { viewModel.showVaultDialog(true) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Screenshots Gallery Section
                    if (!game.screenshots.isNullOrEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "📸 Screenshots (${game.screenshots.size})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                itemsIndexed(game.screenshots) { idx, shot ->
                                    Card(
                                        modifier = Modifier
                                            .width(220.dp)
                                            .height(125.dp)
                                            .clickable { viewModel.openScreenshot(idx) }
                                            .testTag("screenshot_item_$idx"),
                                        shape = RoundedCornerShape(12.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                                    ) {
                                        AsyncImage(
                                            model = shot.image,
                                            contentDescription = "Game screenshot $idx",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // About the Game Overview
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "About the Game",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                val descText = game.description ?: game.shortDescription ?: "No detailed description available."
                                Text(
                                    text = descText,
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    lineHeight = 19.sp,
                                    maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 4
                                )
                                if (descText.length > 200) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (isDescriptionExpanded) "Show Less" else "Read More...",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberCyan,
                                        modifier = Modifier.clickable { isDescriptionExpanded = !isDescriptionExpanded }
                                    )
                                }
                            }
                        }
                    }

                    // Game Specifications Info Card (Publisher, Developer, Release Date, Platform)
                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Game Information",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                InfoRow(label = "Developer", value = game.developer ?: "Unknown")
                                HorizontalDivider(color = BorderDark, modifier = Modifier.padding(vertical = 8.dp))
                                InfoRow(label = "Publisher", value = game.publisher ?: "Unknown")
                                HorizontalDivider(color = BorderDark, modifier = Modifier.padding(vertical = 8.dp))
                                InfoRow(label = "Release Date", value = game.releaseDate ?: "Unknown")
                                HorizontalDivider(color = BorderDark, modifier = Modifier.padding(vertical = 8.dp))
                                InfoRow(label = "Platform", value = game.platform ?: "PC")
                            }
                        }
                    }

                    // Minimum System Requirements (for PC titles)
                    game.minimumSystemRequirements?.let { req ->
                        item {
                            Spacer(modifier = Modifier.height(14.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = CyberCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Minimum System Requirements",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    if (!req.os.isNullOrBlank()) {
                                        SpecRow(name = "OS", value = req.os)
                                    }
                                    if (!req.processor.isNullOrBlank()) {
                                        SpecRow(name = "Processor", value = req.processor)
                                    }
                                    if (!req.memory.isNullOrBlank()) {
                                        SpecRow(name = "Memory", value = req.memory)
                                    }
                                    if (!req.graphics.isNullOrBlank()) {
                                        SpecRow(name = "Graphics", value = req.graphics)
                                    }
                                    if (!req.storage.isNullOrBlank()) {
                                        SpecRow(name = "Storage", value = req.storage)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Fullscreen Screenshot Modal
        state.activeScreenshotIndex?.let { index ->
            val shot = state.game?.screenshots?.getOrNull(index)
            if (shot != null) {
                FullscreenScreenshotModal(
                    imageUrl = shot.image,
                    onDismiss = { viewModel.closeScreenshot() }
                )
            }
        }

        // Add/Edit Vault Dialog
        if (state.showVaultEditDialog) {
            AddToVaultDialog(
                initialStatus = state.vaultEntity?.status ?: "Want to Play",
                initialRating = state.vaultEntity?.rating ?: 0,
                initialNotes = state.vaultEntity?.personalNotes ?: "",
                isAlreadySaved = state.isInVault,
                onSave = { status, rating, notes ->
                    viewModel.saveToVault(status, rating, notes)
                },
                onDelete = {
                    viewModel.removeFromVault()
                },
                onDismiss = {
                    viewModel.showVaultDialog(false)
                }
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = TextMuted)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}

@Composable
private fun SpecRow(name: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = name,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = CyberCyan
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 16.sp
        )
    }
}

private fun openGameUrl(context: Context, url: String?) {
    if (url.isNullOrBlank()) {
        Toast.makeText(context, "No game URL available", Toast.LENGTH_SHORT).show()
        return
    }
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to open link in browser", Toast.LENGTH_SHORT).show()
    }
}

private fun shareGame(context: Context, game: GameDetailDto) {
    try {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "Check out ${game.title} - Free to Play! ${game.gameUrl ?: game.profileUrl}"
            )
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "Share ${game.title}"))
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to share game", Toast.LENGTH_SHORT).show()
    }
}
