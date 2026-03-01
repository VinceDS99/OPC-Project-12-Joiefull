package com.example.joiefull.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.joiefull.domain.model.Clothes
import java.util.Locale

@Composable
fun ClothesItemCard(
    clothes: Clothes,
    onClick: () -> Unit,
    isFavorite: Boolean = false,
    likeDelta: Int = 0,
    onFavoriteClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val displayedLikes = clothes.likes + likeDelta
    val favoriteLabel = if (isFavorite)
        "Retirer ${clothes.name} des favoris"
    else
        "Ajouter ${clothes.name} aux favoris"

    Column(
        modifier = modifier
            .clickable(
                onClick = onClick,
                onClickLabel = "Voir les détails de ${clothes.name}"
            )
            .semantics {
                contentDescription =
                    "${clothes.name}, ${formatPrice(clothes.price)}, " +
                            "note ${clothes.rating} sur 5, $displayedLikes j'aime"
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.75f)
                .clip(RoundedCornerShape(16.dp))
        ) {
            AsyncImage(
                model = clothes.imageUrl,
                contentDescription = null, // décrit par le semantics parent
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Badge likes — zone de touch 48dp minimum
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .size(48.dp)
                    .clickable(
                        onClick = onFavoriteClick,
                        onClickLabel = favoriteLabel,
                        role = Role.Button
                    )
                    .semantics { contentDescription = "$favoriteLabel, $displayedLikes j'aime" },
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.9f),
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .clearAndSetSemantics {} // sémantique gérée par le Box parent
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (isFavorite) Color.Black else Color(0xFF555555)
                        )
                        Text(
                            text = "$displayedLikes",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF333333)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Textes décoratifs — sémantique portée par le Column parent
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clearAndSetSemantics {},
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = clothes.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(Icons.Default.Star, contentDescription = null,
                    tint = Color(0xFFFFBF00), modifier = Modifier.size(14.dp))
                Text(text = clothes.rating.toString(),
                    style = MaterialTheme.typography.labelMedium)
            }
        }

        Row(
            modifier = Modifier.clearAndSetSemantics {},
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(formatPrice(clothes.price), style = MaterialTheme.typography.bodySmall)
            if (clothes.hasDiscount) {
                Text(
                    text = formatPrice(clothes.originalPrice),
                    style = MaterialTheme.typography.bodySmall.copy(
                        textDecoration = TextDecoration.LineThrough
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

private fun formatPrice(price: Double): String {
    return if (price % 1.0 == 0.0) "${price.toInt()}€"
    else String.format(Locale.FRENCH, "%.2f€", price)
}