package com.example.toollink

data class Equipment(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val subCategory: String = "",
    val description: String = "",
    val pricePerDay: Double = 0.0,
    val ownerId: String = "",
    val imageUrl: String = "",
    val isAvailable: Boolean = true
)
