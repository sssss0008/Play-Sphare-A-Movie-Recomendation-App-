package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.GameVaultEntity
import com.example.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VaultStats(
    val total: Int = 0,
    val wantToPlay: Int = 0,
    val playing: Int = 0,
    val completed: Int = 0
)

data class VaultUiState(
    val allGames: List<GameVaultEntity> = emptyList(),
    val displayedGames: List<GameVaultEntity> = emptyList(),
    val selectedStatus: String = "All",
    val searchQuery: String = "",
    val stats: VaultStats = VaultStats()
)

class VaultViewModel(
    private val repository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VaultUiState())
    val uiState: StateFlow<VaultUiState> = _uiState.asStateFlow()

    val statuses = listOf("All", "Want to Play", "Playing", "Completed", "Favorited")

    init {
        observeVault()
    }

    private fun observeVault() {
        viewModelScope.launch {
            repository.getAllVaultGames().collectLatest { list ->
                val stats = VaultStats(
                    total = list.size,
                    wantToPlay = list.count { it.status == "Want to Play" },
                    playing = list.count { it.status == "Playing" },
                    completed = list.count { it.status == "Completed" }
                )
                _uiState.update { current ->
                    current.copy(
                        allGames = list,
                        stats = stats,
                        displayedGames = filterList(list, current.selectedStatus, current.searchQuery)
                    )
                }
            }
        }
    }

    fun setStatusFilter(status: String) {
        _uiState.update { current ->
            current.copy(
                selectedStatus = status,
                displayedGames = filterList(current.allGames, status, current.searchQuery)
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { current ->
            current.copy(
                searchQuery = query,
                displayedGames = filterList(current.allGames, current.selectedStatus, query)
            )
        }
    }

    fun removeGame(id: Int) {
        viewModelScope.launch {
            repository.removeFromVault(id)
        }
    }

    fun updateStatus(game: GameVaultEntity, newStatus: String) {
        viewModelScope.launch {
            repository.saveToVault(game.copy(status = newStatus))
        }
    }

    private fun filterList(
        list: List<GameVaultEntity>,
        status: String,
        query: String
    ): List<GameVaultEntity> {
        var result = list
        if (status != "All") {
            result = result.filter { it.status.equals(status, ignoreCase = true) }
        }
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            result = result.filter {
                it.title.lowercase().contains(q) ||
                        it.genre.lowercase().contains(q) ||
                        it.personalNotes.lowercase().contains(q)
            }
        }
        return result
    }
}
