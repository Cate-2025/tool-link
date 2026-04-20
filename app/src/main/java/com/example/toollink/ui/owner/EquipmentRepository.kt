package com.example.toollink.ui.owner

import android.net.Uri
import kotlinx.coroutines.delay

interface EquipmentRepository {
    suspend fun registerOwner(registration: OwnerRegistration): Result<Unit>
    
    suspend fun registerEquipment(
        name: String,
        description: String,
        price: Double,
        availability: String,
        imageUri: Uri?
    ): Result<Unit>
}

class EquipmentRepositoryImpl : EquipmentRepository {
    override suspend fun registerOwner(registration: OwnerRegistration): Result<Unit> {
        return try {
            delay(2000) // Simulate complex document upload
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerEquipment(
        name: String,
        description: String,
        price: Double,
        availability: String,
        imageUri: Uri?
    ): Result<Unit> {
        return try {
            delay(1500)
            if (name.contains("error", ignoreCase = true)) {
                Result.failure(Exception("Backend rejected the equipment"))
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
