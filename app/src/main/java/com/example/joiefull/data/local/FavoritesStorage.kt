package com.example.joiefull.data.local

import android.content.Context
import androidx.core.content.edit

class FavoritesStorage(context: Context) {

    private val prefs = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)

    fun isFavorite(clothesId: Int): Boolean = prefs.getBoolean("fav_$clothesId", false)

    fun setFavorite(clothesId: Int, isFavorite: Boolean) {
        prefs.edit { putBoolean("fav_$clothesId", isFavorite) }
    }

    fun getAllFavorites(): Set<Int> {
        return prefs.all
            .filter { it.value == true }
            .keys
            .map { it.removePrefix("fav_").toIntOrNull() }
            .filterNotNull()
            .toSet()
    }
}