package com.example.joiefull.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.joiefull.domain.model.Clothes
import com.example.joiefull.domain.model.ClothesCategory

@Composable
fun ClothesCategorySection(
    category: ClothesCategory,
    clothes: List<Clothes>,
    onClothesClick: (Int) -> Unit,
    onFavoriteClick: (Int) -> Unit,
    favoritedIds: Set<Int>,
    likeDeltas: Map<Int, Int>,
    isTablet: Boolean = false,
    modifier: Modifier = Modifier
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = if (isTablet) screenWidth * 0.22f else screenWidth * 0.5f

    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = category.displayName,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .semantics { heading() }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = clothes, key = { it.id }) { item ->
                ClothesItemCard(
                    clothes = item,
                    onClick = { onClothesClick(item.id) },
                    isFavorite = favoritedIds.contains(item.id),
                    likeDelta = likeDeltas[item.id] ?: 0,
                    onFavoriteClick = { onFavoriteClick(item.id) },
                    modifier = Modifier.width(cardWidth)
                )
            }
        }
    }
}