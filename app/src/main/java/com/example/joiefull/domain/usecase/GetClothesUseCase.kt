package com.example.joiefull.domain.usecase

import com.example.joiefull.domain.model.Clothes
import com.example.joiefull.domain.repository.ClothesRepository

class GetClothesUseCase(
    private val repository: ClothesRepository
) {
    suspend operator fun invoke(): Result<List<Clothes>> {
        return repository.getClothes()
    }
}