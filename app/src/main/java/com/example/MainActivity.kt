package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.PlaySphereDatabase
import com.example.data.remote.NetworkClient
import com.example.data.repository.GameRepository
import com.example.ui.screens.GameDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.VaultScreen
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PlaySphereTheme
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.GameDetailViewModel
import com.example.ui.viewmodel.HomeViewModel
import com.example.ui.viewmodel.VaultViewModel
import com.example.ui.viewmodel.ViewModelFactory

sealed class Screen {
    object Explore : Screen()
    object Vault : Screen()
    data class Detail(val gameId: Int) : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PlaySphereTheme {
                PlaySphereApp()
            }
        }
    }
}

@Composable
fun PlaySphereApp() {
    val context = LocalContext.current
    val repository = remember {
        val database = PlaySphereDatabase.getDatabase(context)
        GameRepository(
            api = NetworkClient.api,
            vaultDao = database.gameVaultDao()
        )
    }

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Explore) }
    var previousScreen by remember { mutableStateOf<Screen>(Screen.Explore) }

    val homeViewModel: HomeViewModel = viewModel(
        factory = ViewModelFactory(repository)
    )
    val vaultViewModel: VaultViewModel = viewModel(
        factory = ViewModelFactory(repository)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        bottomBar = {
            // Only show bottom navigation on top-level tabs (Explore, Vault)
            if (currentScreen !is Screen.Detail) {
                NavigationBar(
                    containerColor = SurfaceDark,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    val isExplore = currentScreen is Screen.Explore
                    val isVault = currentScreen is Screen.Vault

                    NavigationBarItem(
                        selected = isExplore,
                        onClick = { currentScreen = Screen.Explore },
                        icon = {
                            Icon(
                                imageVector = if (isExplore) Icons.Default.Explore else Icons.Outlined.Explore,
                                contentDescription = "Explore",
                                tint = if (isExplore) CyberCyan else TextMuted
                            )
                        },
                        label = {
                            Text(
                                text = "Explore",
                                fontSize = 11.sp,
                                fontWeight = if (isExplore) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = CyberCyan.copy(alpha = 0.15f),
                            selectedTextColor = CyberCyan,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("tab_explore")
                    )

                    NavigationBarItem(
                        selected = isVault,
                        onClick = { currentScreen = Screen.Vault },
                        icon = {
                            Icon(
                                imageVector = if (isVault) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Vault",
                                tint = if (isVault) NeonPurple else TextMuted
                            )
                        },
                        label = {
                            Text(
                                text = "My Vault",
                                fontSize = 11.sp,
                                fontWeight = if (isVault) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = NeonPurple.copy(alpha = 0.15f),
                            selectedTextColor = NeonPurple,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("tab_vault")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.Explore -> {
                    HomeScreen(
                        viewModel = homeViewModel,
                        onGameClick = { id ->
                            previousScreen = Screen.Explore
                            currentScreen = Screen.Detail(id)
                        }
                    )
                }
                is Screen.Vault -> {
                    VaultScreen(
                        viewModel = vaultViewModel,
                        onGameClick = { id ->
                            previousScreen = Screen.Vault
                            currentScreen = Screen.Detail(id)
                        },
                        onNavigateExplore = {
                            currentScreen = Screen.Explore
                        }
                    )
                }
                is Screen.Detail -> {
                    val detailViewModel: GameDetailViewModel = viewModel(
                        key = "detail_${screen.gameId}",
                        factory = ViewModelFactory(repository, screen.gameId)
                    )
                    GameDetailScreen(
                        viewModel = detailViewModel,
                        onBack = {
                            currentScreen = previousScreen
                        }
                    )
                }
            }
        }
    }
}
