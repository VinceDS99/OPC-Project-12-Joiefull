package com.example.joiefull.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.joiefull.presentation.components.ClothesCategorySection

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    onClothesClick: (Int) -> Unit = {},
    isTablet: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    androidx.compose.foundation.layout.Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> CircularProgressIndicator()
            uiState.error != null -> Text(
                text = "Erreur : ${uiState.error}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 32.dp, bottom = 24.dp)
                ) {
                    uiState.categorizedClothes.forEach { (category, clothesList) ->
                        item(key = category.name) {
                            ClothesCategorySection(
                                category = category,
                                clothes = clothesList,
                                onClothesClick = onClothesClick,
                                onFavoriteClick = { id -> viewModel.toggleFavorite(id) },
                                favoritedIds = uiState.favoritedIds,
                                likeDeltas = uiState.localLikeDeltas,
                                isTablet = isTablet
                            )
                        }
                    }
                }
            }
        }
    }
}