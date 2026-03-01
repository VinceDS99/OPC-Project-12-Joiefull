package com.example.joiefull.presentation.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.joiefull.data.local.FavoritesStorage
import com.example.joiefull.data.remote.RetrofitInstance
import com.example.joiefull.data.repository.ClothesRepositoryImpl
import com.example.joiefull.domain.model.Clothes
import com.example.joiefull.domain.model.ClothesCategory
import com.example.joiefull.domain.usecase.GetClothesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val categorizedClothes: Map<ClothesCategory, List<Clothes>> = emptyMap(),
    val error: String? = null,
    val selectedClothesId: Int? = null,
    val favoritedIds: Set<Int> = emptySet(),  // IDs des articles likés par l'utilisateur
    val localLikeDeltas: Map<Int, Int> = emptyMap() // +1 ou -1 par article
)

class HomeViewModel(
    application: Application,
    private val getClothesUseCase: GetClothesUseCase
) : AndroidViewModel(application) {

    private val favoritesStorage = FavoritesStorage(application)

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadClothes()
    }

    private fun loadClothes() {
        viewModelScope.launch {
            _uiState.value = HomeUiState(isLoading = true)
            getClothesUseCase()
                .onSuccess { clothes ->
                    val grouped = clothes
                        .groupBy { it.category }
                        .mapValues { (_, items) -> items.sortedByDescending { it.likes } }
                        .toSortedMap(compareBy { it.ordinal })
                    val favorites = favoritesStorage.getAllFavorites()
                    _uiState.value = HomeUiState(
                        categorizedClothes = grouped,
                        favoritedIds = favorites
                    )
                }
                .onFailure { error ->
                    _uiState.value = HomeUiState(error = error.message ?: "Erreur inconnue")
                }
        }
    }

    fun toggleFavorite(clothesId: Int) {
        val current = _uiState.value
        val isFav = current.favoritedIds.contains(clothesId)
        val newFavs = if (isFav) current.favoritedIds - clothesId
        else current.favoritedIds + clothesId
        val delta = if (isFav) -1 else +1
        val newDeltas = current.localLikeDeltas.toMutableMap()
        newDeltas[clothesId] = (newDeltas[clothesId] ?: 0) + delta
        favoritesStorage.setFavorite(clothesId, !isFav)
        _uiState.value = current.copy(
            favoritedIds = newFavs,
            localLikeDeltas = newDeltas
        )
    }

    fun selectClothes(id: Int) {
        _uiState.value = _uiState.value.copy(selectedClothesId = id)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                throw UnsupportedOperationException("Use Factory with Application")
            }
        }
    }
}