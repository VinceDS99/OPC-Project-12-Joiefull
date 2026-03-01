package com.example.joiefull.domain.repository

import com.example.joiefull.domain.model.Clothes

interface ClothesRepository {
    suspend fun getClothes(): Result<List<Clothes>>
}