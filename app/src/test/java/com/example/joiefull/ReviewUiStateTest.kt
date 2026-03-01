package com.example.joiefull

import com.example.joiefull.presentation.detail.ReviewUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReviewUiStateTest {

    @Test
    fun `initial state should have userRating 0`() {
        assertEquals(0, ReviewUiState().userRating)
    }

    @Test
    fun `initial state should have empty comment`() {
        assertEquals("", ReviewUiState().comment)
    }

    @Test
    fun `initial state should not be submitted`() {
        assertFalse(ReviewUiState().isSubmitted)
    }

    @Test
    fun `setting rating should update userRating`() {
        assertEquals(4, ReviewUiState().copy(userRating = 4).userRating)
    }

    @Test
    fun `rating should accept values from 1 to 5`() {
        for (star in 1..5) {
            assertEquals(star, ReviewUiState().copy(userRating = star).userRating)
        }
    }

    @Test
    fun `setting comment should update comment field`() {
        assertEquals("Très bel article !", ReviewUiState().copy(comment = "Très bel article !").comment)
    }

    @Test
    fun `comment can be empty when submitting`() {
        val state = ReviewUiState(userRating = 3, comment = "", isSubmitted = true)
        assertTrue(state.isSubmitted)
        assertEquals("", state.comment)
    }

    @Test
    fun `submitting review should set isSubmitted to true`() {
        assertTrue(ReviewUiState(userRating = 5, comment = "Parfait", isSubmitted = true).isSubmitted)
    }

    @Test
    fun `deleting review should reset to initial state`() {
        val reset = ReviewUiState()
        assertEquals(0, reset.userRating)
        assertEquals("", reset.comment)
        assertFalse(reset.isSubmitted)
    }

}