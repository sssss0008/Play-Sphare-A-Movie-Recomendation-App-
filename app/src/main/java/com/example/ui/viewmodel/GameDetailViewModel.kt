package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.GameVaultEntity
import com.example.data.model.GameDetailDto
import com.example.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameDetailUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val game: GameDetailDto? = null,
    val vaultEntity: GameVaultEntity? = null,
    val isInVault: Boolean = false,
    val activeScreenshotIndex: Int? = null,
    val showVaultEditDialog: Boolean = false
)

class GameDetailViewModel(
    private val repository: GameRepository,
    private val gameId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameDetailUiState())
    val uiState: StateFlow<GameDetailUiState> = _uiState.asStateFlow()

    init {
        observeVaultItem()
        loadDetail()
    }

    private fun observeVaultItem() {
        viewModelScope.launch {
            repository.getVaultGameById(gameId).collectLatest { entity ->
                _uiState.update {
                    it.copy(
                        vaultEntity = entity,
                        isInVault = entity != null
                    )
                }
            }
        }
    }

    fun loadDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.fetchGameDetail(gameId)
            result.onSuccess { detail ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        game = detail,
                        errorMessage = null
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = err.localizedMessage ?: "Failed to load game details."
                    )
                }
            }
        }
    }

    fun showVaultDialog(show: Boolean) {
        _uiState.update { it.copy(showVaultEditDialog = show) }
    }

    fun saveToVault(status: String, rating: Int, notes: String) {
        val game = _uiState.value.game ?: return
        viewModelScope.launch {
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
                status = status,
                rating = rating,
                personalNotes = notes
            )
            repository.saveToVault(entity)
            _uiState.update { it.copy(showVaultEditDialog = false) }
        }
    }

    fun removeFromVault() {
        viewModelScope.launch {
            repository.removeFromVault(gameId)
            _uiState.update { it.copy(showVaultEditDialog = false) }
        }
    }

    fun openScreenshot(index: Int) {
        _uiState.update { it.copy(activeScreenshotIndex = index) }
    }

    fun closeScreenshot() {
        _uiState.update { it.copy(activeScreenshotIndex = null) }
    }
}
