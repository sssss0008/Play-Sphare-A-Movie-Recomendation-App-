package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.GameVaultEntity
import com.example.data.model.GameDto
import com.example.data.repository.GameRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val allGames: List<GameDto> = emptyList(),
    val filteredGames: List<GameDto> = emptyList(),
    val popularGames: List<GameDto> = emptyList(),
    val featuredGame: GameDto? = null,
    val searchQuery: String = "",
    val selectedPlatform: String = "all", // "all", "pc", "browser"
    val selectedCategory: String = "All",
    val selectedSort: String = "popularity", // "popularity", "release-date", "alphabetical", "relevance"
    val vaultIds: Set<Int> = emptySet(),
    val isRollingMystery: Boolean = false,
    val mysteryGame: GameDto? = null
)

class HomeViewModel(
    private val repository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    val categories = listOf(
        "All", "Shooter", "MMORPG", "Battle Royale", "Strategy", "MOBA",
        "Anime", "Sci-Fi", "Survival", "Action-RPG", "Racing", "Fighting", "Horror"
    )

    val platforms = listOf(
        "all" to "All Platforms",
        "pc" to "PC (Windows)",
        "browser" to "Web Browser"
    )

    val sortOptions = listOf(
        "popularity" to "Most Popular",
        "release-date" to "Latest Release",
        "alphabetical" to "Alphabetical (A-Z)",
        "relevance" to "Relevance"
    )

    init {
        observeVault()
        loadGames()
    }

    private fun observeVault() {
        viewModelScope.launch {
            repository.getAllVaultGames().collectLatest { vaultList ->
                val ids = vaultList.map { it.id }.toSet()
                _uiState.update { it.copy(vaultIds = ids) }
            }
        }
    }

    fun loadGames(isRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = !isRefresh,
                    isRefreshing = isRefresh,
                    errorMessage = null
                )
            }

            val result = repository.fetchGames(
                platform = if (_uiState.value.selectedPlatform == "all") null else _uiState.value.selectedPlatform,
                category = if (_uiState.value.selectedCategory == "All") null else _uiState.value.selectedCategory,
                sortBy = _uiState.value.selectedSort,
                forceRefresh = isRefresh
            )

            result.onSuccess { games ->
                val popular = games.take(10)
                val featured = games.firstOrNull()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        allGames = games,
                        popularGames = popular,
                        featuredGame = featured,
                        filteredGames = filterGamesList(games, it.searchQuery),
                        errorMessage = null
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = err.localizedMessage ?: "Failed to load games. Check internet connection."
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(150) // debounce
            _uiState.update {
                it.copy(filteredGames = filterGamesList(it.allGames, query))
            }
        }
    }

    fun onPlatformSelected(platform: String) {
        if (_uiState.value.selectedPlatform == platform) return
        _uiState.update { it.copy(selectedPlatform = platform) }
        loadGames()
    }

    fun onCategorySelected(category: String) {
        if (_uiState.value.selectedCategory == category) return
        _uiState.update { it.copy(selectedCategory = category) }
        loadGames()
    }

    fun onSortSelected(sort: String) {
        if (_uiState.value.selectedSort == sort) return
        _uiState.update { it.copy(selectedSort = sort) }
        loadGames()
    }

    fun toggleVault(game: GameDto) {
        viewModelScope.launch {
            if (_uiState.value.vaultIds.contains(game.id)) {
                repository.removeFromVault(game.id)
            } else {
                val entity = GameVaultEntity(
                    id = game.id,
                    title = game.title,
                    thumbnail = game.thumbnail,
                    shortDescription = game.shortDescription ?: "",
                    genre = game.genre ?: "Game",
                    platform = game.platform ?: "PC",
                    publisher = game.publisher ?: "",
                    developer = game.developer ?: "",
                    releaseDate = game.releaseDate ?: "",
                    gameUrl = game.gameUrl ?: "",
                    status = "Want to Play"
                )
                repository.saveToVault(entity)
            }
        }
    }

    fun rollMysteryGame() {
        viewModelScope.launch {
            val currentGames = _uiState.value.allGames
            if (currentGames.isEmpty()) return@launch
            _uiState.update { it.copy(isRollingMystery = true, mysteryGame = null) }
            delay(800) // Brief roulette animation pause
            val picked = currentGames.random()
            _uiState.update { it.copy(isRollingMystery = false, mysteryGame = picked) }
        }
    }

    fun dismissMysteryGame() {
        _uiState.update { it.copy(mysteryGame = null, isRollingMystery = false) }
    }

    private fun filterGamesList(games: List<GameDto>, query: String): List<GameDto> {
        if (query.isBlank()) return games
        val q = query.trim().lowercase()
        return games.filter { game ->
            game.title.lowercase().contains(q) ||
                    (game.genre?.lowercase()?.contains(q) == true) ||
                    (game.shortDescription?.lowercase()?.contains(q) == true) ||
                    (game.developer?.lowercase()?.contains(q) == true) ||
                    (game.publisher?.lowercase()?.contains(q) == true)
        }
    }
}
