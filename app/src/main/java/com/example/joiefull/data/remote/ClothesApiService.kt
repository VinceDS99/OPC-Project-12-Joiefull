package com.example.joiefull.data.remote

import retrofit2.http.GET

interface ClothesApiService {
    @GET("api/clothes.json")
    suspend fun getClothes(): List<ClothesDto>
}