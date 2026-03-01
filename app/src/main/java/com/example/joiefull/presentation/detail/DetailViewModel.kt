package com.example.joiefull.presentation.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.joiefull.data.local.UserReviewStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReviewUiState(
    val userRating: Int = 0,
    val comment: String = "",
    val isSubmitted: Boolean = false
)

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val storage = UserReviewStorage(application)

    private val _reviewState = MutableStateFlow(ReviewUiState())
    val reviewState: StateFlow<ReviewUiState> = _reviewState.asStateFlow()

    fun loadReview(clothesId: Int) {
        viewModelScope.launch {
            val rating = storage.getRating(clothesId)
            val comment = storage.getComment(clothesId)
            _reviewState.value = ReviewUiState(
                userRating = rating,
                comment = comment,
                isSubmitted = rating > 0
            )
        }
    }

    fun setRating(rating: Int) {
        _reviewState.value = _reviewState.value.copy(userRating = rating)
    }

    fun setComment(comment: String) {
        _reviewState.value = _reviewState.value.copy(comment = comment)
    }

    fun deleteReview(clothesId: Int) {
        storage.saveRating(clothesId, 0)
        storage.saveComment(clothesId, "")
        _reviewState.value = ReviewUiState()
    }

    fun submitReview(clothesId: Int) {
        val state = _reviewState.value
        if (state.userRating == 0) return
        storage.saveRating(clothesId, state.userRating)
        storage.saveComment(clothesId, state.comment)
        _reviewState.value = state.copy(isSubmitted = true)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                throw UnsupportedOperationException("Use factory with application")
            }
        }
    }
}