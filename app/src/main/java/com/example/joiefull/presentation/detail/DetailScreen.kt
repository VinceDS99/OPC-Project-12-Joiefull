package com.example.joiefull.presentation.detail

import android.app.Application
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable

import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable

import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.joiefull.presentation.components.ShareBottomSheet
import com.example.joiefull.presentation.home.HomeViewModel
import java.util.Locale

@Composable
fun DetailScreen(
    clothesId: Int,
    viewModel: HomeViewModel,
    onBack: () -> Unit,
    isTablet: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val detailViewModel: DetailViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
                DetailViewModel(application) as T
        }
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val reviewState by detailViewModel.reviewState.collectAsStateWithLifecycle()
    val clothes = uiState.categorizedClothes.values.flatten().find { it.id == clothesId }

    LaunchedEffect(clothesId) { detailViewModel.loadReview(clothesId) }

    var showShareSheet by remember { mutableStateOf(false) }
    var isImageZoomed by remember { mutableStateOf(false) }
    val imageFraction = if (isTablet) 0.85f else 0.9f

    if (clothes == null) {
        Box(
            Modifier.fillMaxSize().semantics { contentDescription = "Chargement en cours" },
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }
        return
    }

    val isFav = uiState.favoritedIds.contains(clothesId)
    val delta = uiState.localLikeDeltas[clothesId] ?: 0
    val displayedLikes = clothes.likes + delta
    val favoriteLabel = if (isFav) "Retirer des favoris, $displayedLikes j'aime"
    else "Ajouter aux favoris, $displayedLikes j'aime"

    val displayedRating = if (reviewState.userRating > 0) {
        Math.round(((clothes.rating + reviewState.userRating) / 2.0) * 10) / 10.0
    } else clothes.rating

    // Overlay plein écran au clic sur l'image
    if (isImageZoomed) {
        var scale by remember { mutableStateOf(1f) }
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        val transformState = rememberTransformableState { zoomChange, panChange, _ ->
            scale = (scale * zoomChange).coerceIn(1f, 5f)
            offsetX += panChange.x
            offsetY += panChange.y
        }

        Dialog(
            onDismissRequest = { isImageZoomed = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
                    .clickable(onClickLabel = "Fermer l'image") { isImageZoomed = false },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = clothes.imageUrl,
                    contentDescription = clothes.imageDescription,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.9f)
                        .scale(scale)
                        .offset { androidx.compose.ui.unit.IntOffset(offsetX.toInt(), offsetY.toInt()) }
                        .transformable(state = transformState)
                        .clickable(onClickLabel = "Fermer l'image") { isImageZoomed = false }
                )
            }
        }
    }

    if (showShareSheet) {
        ShareBottomSheet(
            clothesId = clothesId,
            clothesName = clothes.name,
            onDismiss = { showShareSheet = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Image
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp, bottom = 12.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(imageFraction)
                    .aspectRatio(0.85f)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(onClickLabel = "Agrandir l'image") { isImageZoomed = true }
                    .semantics { contentDescription = clothes.imageDescription }
            ) {
                AsyncImage(
                    model = clothes.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (!isTablet) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                            .size(48.dp) // 48dp minimum
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour à la liste",
                            modifier = Modifier.size(26.dp),
                            tint = Color.Black
                        )
                    }
                }

                IconButton(
                    onClick = { showShareSheet = true },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(48.dp)
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Partager ${clothes.name}",
                        modifier = Modifier.size(26.dp),
                        tint = Color.Black
                    )
                }

                // Badge favoris
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .clickable(
                            onClick = { viewModel.toggleFavorite(clothesId) },
                            onClickLabel = favoriteLabel,
                            role = Role.Button
                        )
                        .semantics { contentDescription = favoriteLabel },
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier
                            .clearAndSetSemantics {}
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(26.dp),
                            tint = Color.Black
                        )
                        Text("$displayedLikes", fontSize = 12.sp, color = Color.Black)
                    }
                }
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Nom + note — annonce complète pour le lecteur d'écran
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics(mergeDescendants = true) {
                        contentDescription = "${clothes.name}, note $displayedRating sur 5"
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = clothes.name, fontSize = 15.sp,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .clearAndSetSemantics {}
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier.clearAndSetSemantics {}
                ) {
                    Icon(Icons.Default.Star, contentDescription = null,
                        tint = Color(0xFFFFBF00), modifier = Modifier.size(26.dp))
                    Text(displayedRating.toString(), fontSize = 13.sp)
                }
            }

            // Prix
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics(mergeDescendants = true) {
                        contentDescription = if (clothes.hasDiscount)
                            "Prix : ${formatPrice(clothes.price)}, prix original : ${formatPrice(clothes.originalPrice)}"
                        else "Prix : ${formatPrice(clothes.price)}"
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(formatPrice(clothes.price), fontSize = 13.sp,
                    modifier = Modifier.clearAndSetSemantics {})
                if (clothes.hasDiscount) {
                    Text(
                        text = formatPrice(clothes.originalPrice), fontSize = 13.sp,
                        style = LocalTextStyle.current.copy(textDecoration = TextDecoration.LineThrough),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.clearAndSetSemantics {}
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = clothes.imageDescription, fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            // Section avis
            if (reviewState.isSubmitted) {
                UserReviewCard(
                    userName = "John Doe",
                    rating = reviewState.userRating,
                    comment = reviewState.comment,
                    onDelete = { detailViewModel.deleteReview(clothesId) }
                )
            } else {
                ReviewForm(
                    currentRating = reviewState.userRating,
                    comment = reviewState.comment,
                    onRatingChange = { detailViewModel.setRating(it) },
                    onCommentChange = { detailViewModel.setComment(it) },
                    onSubmit = { detailViewModel.submitReview(clothesId) }
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Formulaire de notation ──────────────────────────────────────────────────

@Composable
private fun ReviewForm(
    currentRating: Int,
    comment: String,
    onRatingChange: (Int) -> Unit,
    onCommentChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(modifier = Modifier.size(38.dp), shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }

            // Étoiles avec zone de touch 48dp et sémantique
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (star in 1..5) {
                    val filled = star <= currentRating
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable(
                                onClick = { onRatingChange(star) },
                                onClickLabel = "Donner $star étoile${if (star > 1) "s" else ""}",
                                role = Role.Button
                            )
                            .semantics {
                                contentDescription = if (filled)
                                    "Étoile $star sur 5, sélectionnée"
                                else
                                    "Étoile $star sur 5, non sélectionnée"
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        StarShape(filled = filled, size = 36f)
                    }
                }
            }
        }

        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
            color = Color.White, border = ButtonDefaults.outlinedButtonBorder) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .semantics {
                        contentDescription = "Champ commentaire. ${if (comment.isBlank()) "Vide" else comment}"
                    }
            ) {
                if (comment.isEmpty()) {
                    Text("Partagez ici vos impressions sur cette pièce", fontSize = 12.sp,
                        color = Color(0xFF666666),
                        modifier = Modifier.clearAndSetSemantics {})
                }
                BasicTextField(
                    value = comment, onValueChange = onCommentChange,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp)
                        .clearAndSetSemantics {},
                    textStyle = TextStyle(fontSize = 13.sp, color = Color.Black),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                )
            }
        }

        Button(
            onClick = onSubmit,
            enabled = currentRating > 0,
            modifier = Modifier
                .align(Alignment.End)
                .heightIn(min = 48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF99F43),
                disabledContainerColor = Color(0xFFCCCCCC),
                contentColor = Color.White,
                disabledContentColor = Color.White
            )
        ) {
            Text("Valider mon avis", fontSize = 13.sp)
        }
    }
}

// ── Avis soumis ─────────────────────────────────────────────────────────────

@Composable
private fun UserReviewCard(
    userName: String,
    rating: Int,
    comment: String,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Supprimer l'avis", fontSize = 15.sp) },
            text = { Text("Voulez-vous supprimer votre avis et votre note ?", fontSize = 13.sp) },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; onDelete() }) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Annuler") }
            }
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.semantics(mergeDescendants = true) {
            contentDescription = "Avis de $userName, $rating étoile${if (rating > 1) "s" else ""}${if (comment.isNotBlank()) ", commentaire : $comment" else ""}"
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(modifier = Modifier.size(34.dp), shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(userName, fontSize = 13.sp, style = MaterialTheme.typography.labelLarge)
                    // Poubelle — 48dp touch target
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable(
                                onClick = { showDeleteDialog = true },
                                onClickLabel = "Supprimer mon avis",
                                role = Role.Button
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                    }
                }

                // Étoiles de la note
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier.clearAndSetSemantics {}) {
                    for (star in 1..5) {
                        Canvas(modifier = Modifier.size(16.dp)) {
                            val path = androidx.compose.ui.graphics.Path()
                            val cx = size.width / 2; val cy = size.height / 2
                            val outer = size.minDimension / 2 * 0.9f
                            val inner = outer * 0.42f
                            for (i in 0 until 10) {
                                val angle = Math.PI / 5 * i - Math.PI / 2
                                val r = if (i % 2 == 0) outer else inner
                                val x = cx + (r * Math.cos(angle)).toFloat()
                                val y = cy + (r * Math.sin(angle)).toFloat()
                                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            }
                            path.close()
                            drawPath(path, color = if (star <= rating)
                                androidx.compose.ui.graphics.Color(0xFFFFBF00)
                            else
                                androidx.compose.ui.graphics.Color(0xFFBBBBBB))
                        }
                    }
                }
            }
        }

        if (comment.isNotBlank()) {
            Text(comment, fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                lineHeight = 18.sp,
                modifier = Modifier
                    .padding(start = 44.dp)
                    .clearAndSetSemantics {})
        }
    }
}

// ── Canvas étoile réutilisable ───────────────────────────────────────────────

@Composable
private fun StarShape(filled: Boolean, size: Float) {
    Canvas(modifier = Modifier.size(size.dp)) {
        val path = androidx.compose.ui.graphics.Path()
        val cx = this.size.width / 2; val cy = this.size.height / 2
        val outer = this.size.minDimension / 2 * 0.9f
        val inner = outer * 0.42f
        for (i in 0 until 10) {
            val angle = Math.PI / 5 * i - Math.PI / 2
            val r = if (i % 2 == 0) outer else inner
            val x = cx + (r * Math.cos(angle)).toFloat()
            val y = cy + (r * Math.sin(angle)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        if (filled) {
            drawPath(path, color = androidx.compose.ui.graphics.Color(0xFFFFBF00))
        } else {
            drawPath(path, color = androidx.compose.ui.graphics.Color(0xFFBBBBBB),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
        }
    }
}

private fun formatPrice(price: Double): String =
    if (price % 1.0 == 0.0) "${price.toInt()}€"
    else String.format(Locale.FRENCH, "%.2f€", price)