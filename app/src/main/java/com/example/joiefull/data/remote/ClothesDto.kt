package com.example.joiefull.data.remote

import com.google.gson.annotations.SerializedName

data class ClothesDto(
    val id: Int,
    val picture: PictureDto,
    val name: String,
    val category: String,
    val likes: Int,
    val price: Double,
    @SerializedName("original_price")
    val originalPrice: Double
)

data class PictureDto(
    val url: String,
    val description: String
)