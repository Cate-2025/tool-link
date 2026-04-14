package com.example.toollink

import kotlinx.coroutines.delay

class EquipmentRepository {
    // Local mock data instead of Firestore
    private val mockEquipment = mutableListOf(
        Equipment(
            id = "1",
            name = "John Deere 5050D",
            category = "Agriculture",
            subCategory = "Tractors",
            description = "50 HP tractor, perfect for medium scale farming and haulage.",
            pricePerDay = 45.0,
            imageUrl = "tractor_url"
        ),
        Equipment(
            id = "2",
            name = "Disc Plough",
            category = "Agriculture",
            subCategory = "Ploughs",
            description = "3-disc plough for tough soil preparation.",
            pricePerDay = 15.0,
            imageUrl = "plough_url"
        ),
        Equipment(
            id = "3",
            name = "Caterpillar 320 GC",
            category = "Construction",
            subCategory = "Excavators",
            description = "Reliable excavator for digging and lifting projects.",
            pricePerDay = 120.0,
            imageUrl = "excavator_url"
        ),
        Equipment(
            id = "4",
            name = "Tata 407 Pickup",
            category = "Transport",
            subCategory = "Pickups",
            description = "Versatile pickup for moving community goods.",
            pricePerDay = 35.0,
            imageUrl = "pickup_url"
        ),
        Equipment(
            id = "5",
            name = "Yamaha Water Pump",
            category = "Agriculture",
            subCategory = "Irrigation",
            description = "High-flow petrol pump for farm irrigation.",
            pricePerDay = 10.0,
            imageUrl = "pump_url"
        ),
        Equipment(
            id = "6",
            name = "Event Sound System",
            category = "Community",
            subCategory = "Sound",
            description = "Complete PA system with 2 speakers and mixer.",
            pricePerDay = 25.0,
            imageUrl = "sound_url"
        )
    )

    suspend fun getAllEquipment(): List<Equipment> {
        delay(500) // Simulate network delay
        return mockEquipment
    }

    suspend fun getEquipmentByCategory(category: String): List<Equipment> {
        delay(500) // Simulate network delay
        return mockEquipment.filter { it.category == category }
    }

    suspend fun addEquipment(equipment: Equipment): Boolean {
        delay(500) // Simulate network delay
        val newId = (mockEquipment.size + 1).toString()
        mockEquipment.add(equipment.copy(id = newId))
        return true
    }
}
