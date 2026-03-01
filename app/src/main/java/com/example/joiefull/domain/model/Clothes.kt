package com.example.joiefull.domain.model

data class Clothes(
    val id: Int,
    val imageUrl: String,
    val imageDescription: String,
    val name: String,
    val category: ClothesCategory,
    val likes: Int,
    val price: Double,
    val originalPrice: Double
) {
    val hasDiscount: Boolean get() = originalPrice > price

    // Note calculée : 0 likes = 3.0, 100 likes ou plus = 5.0
    val rating: Double get() {
        val raw = (likes.coerceIn(0, 100) / 100.0 * 2.0 + 3.0)
        return Math.round(raw * 10) / 10.0
    }
}

enum class ClothesCategory(val displayName: String) {
    TOPS("Hauts"),
    BOTTOMS("Bas"),
    ACCESSORIES("Sacs"),
    SHOES("Chaussures");

    companion object {
        fun fromString(value: String): ClothesCategory =
            entries.firstOrNull { it.name == value } ?: ACCESSORIES
    }
}