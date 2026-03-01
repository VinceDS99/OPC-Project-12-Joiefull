package com.example.joiefull.data.local

import android.content.Context
import androidx.core.content.edit

class UserReviewStorage(context: Context) {

    private val prefs = context.getSharedPreferences("user_reviews", Context.MODE_PRIVATE)

    fun saveRating(clothesId: Int, rating: Int) {
        prefs.edit { putInt("rating_$clothesId", rating) }
    }

    fun getRating(clothesId: Int): Int {
        return prefs.getInt("rating_$clothesId", 0) // 0 = pas encore noté
    }

    fun saveComment(clothesId: Int, comment: String) {
        prefs.edit { putString("comment_$clothesId", comment) }
    }

    fun getComment(clothesId: Int): String {
        return prefs.getString("comment_$clothesId", "") ?: ""
    }
}