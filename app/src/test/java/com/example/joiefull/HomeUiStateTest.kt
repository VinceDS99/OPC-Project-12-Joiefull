package com.example.joiefull

import com.example.joiefull.presentation.home.HomeUiState
import com.example.joiefull.domain.model.Clothes
import com.example.joiefull.domain.model.ClothesCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests unitaires pour la logique pure de HomeUiState (favoris, sélection).
 */
class HomeUiStateTest {

    private val clothes1 = Clothes(1, "url1", "desc1", "T-shirt", ClothesCategory.TOPS, 10, 29.0, 29.0)
    private val clothes2 = Clothes(2, "url2", "desc2", "Jean",    ClothesCategory.BOTTOMS, 50, 59.0, 89.0)

    private fun baseState() = HomeUiState(
        categorizedClothes = mapOf(
            ClothesCategory.TOPS to listOf(clothes1),
            ClothesCategory.BOTTOMS to listOf(clothes2)
        ),
        favoritedIds = emptySet(),
        localLikeDeltas = emptyMap()
    )

    // ── Favoris ───────────────────────────────────────────────────────────────

    @Test
    fun `adding a favorite should include id in favoritedIds`() {
        val state = baseState()
        val newState = state.copy(favoritedIds = state.favoritedIds + clothes1.id)
        assertTrue(newState.favoritedIds.contains(clothes1.id))
    }

    @Test
    fun `removing a favorite should exclude id from favoritedIds`() {
        val state = baseState().copy(favoritedIds = setOf(clothes1.id))
        val newState = state.copy(favoritedIds = state.favoritedIds - clothes1.id)
        assertFalse(newState.favoritedIds.contains(clothes1.id))
    }

    @Test
    fun `toggling favorite twice should restore original state`() {
        var state = baseState()
        state = state.copy(favoritedIds = state.favoritedIds + clothes1.id)
        state = state.copy(favoritedIds = state.favoritedIds - clothes1.id)
        assertFalse(state.favoritedIds.contains(clothes1.id))
    }

    // ── Sélection tablette ────────────────────────────────────────────────────

    @Test
    fun `initial selectedClothesId should be null`() {
        assertNull(baseState().selectedClothesId)
    }

    @Test
    fun `selecting a clothes should update selectedClothesId`() {
        val state = baseState().copy(selectedClothesId = clothes1.id)
        assertEquals(clothes1.id, state.selectedClothesId)
    }

    @Test
    fun `selecting another clothes should replace selectedClothesId`() {
        val state = baseState()
            .copy(selectedClothesId = clothes1.id)
            .copy(selectedClothesId = clothes2.id)
        assertEquals(clothes2.id, state.selectedClothesId)
    }

    // ── Chargement ────────────────────────────────────────────────────────────

    @Test
    fun `loading state should have empty categories`() {
        val state = HomeUiState(isLoading = true)
        assertTrue(state.categorizedClothes.isEmpty())
    }

    @Test
    fun `error state should expose error message`() {
        val state = HomeUiState(error = "Erreur réseau")
        assertEquals("Erreur réseau", state.error)
    }
}