package com.example.joiefull.data.repository

import com.example.joiefull.data.remote.ClothesApiService
import com.example.joiefull.data.remote.ClothesDto
import com.example.joiefull.domain.model.Clothes
import com.example.joiefull.domain.model.ClothesCategory
import com.example.joiefull.domain.repository.ClothesRepository

class ClothesRepositoryImpl(
    private val api: ClothesApiService
) : ClothesRepository {

    override suspend fun getClothes(): Result<List<Clothes>> {
        return try {
            val dtos = api.getClothes()
            Result.success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun ClothesDto.toDomain() = Clothes(
        id = id,
        imageUrl = picture.url,
        imageDescription = picture.description,
        name = name,
        category = ClothesCategory.fromString(category),
        likes = likes,
        price = price,
        originalPrice = originalPrice
    )
}