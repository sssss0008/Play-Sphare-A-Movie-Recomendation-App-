package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.repository.GameRepository

class ViewModelFactory(
    private val repository: GameRepository,
    private val gameId: Int = -1
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(VaultViewModel::class.java) -> {
                VaultViewModel(repository) as T
            }
            modelClass.isAssignableFrom(GameDetailViewModel::class.java) -> {
                GameDetailViewModel(repository, gameId) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
