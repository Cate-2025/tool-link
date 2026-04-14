package com.example.toollink

import kotlinx.coroutines.delay

class EquipmentRepository {
    // Local mock data with UGX prices and expanded inventory
    private val mockEquipment = mutableListOf(
        // Agriculture
        Equipment(
            id = "1",
            name = "John Deere 5050D",
            category = "Agriculture",
            subCategory = "Tractors",
            description = "50 HP tractor, perfect for medium scale farming and haulage.",
            pricePerDay = 150000.0,
            imageUrl = "tractor_url"
        ),
        Equipment(
            id = "2",
            name = "Disc Plough",
            category = "Agriculture",
            subCategory = "Ploughs",
            description = "3-disc plough for tough soil preparation.",
            pricePerDay = 45000.0,
            imageUrl = "plough_url"
        ),
        Equipment(
            id = "3",
            name = "Yamaha Water Pump",
            category = "Agriculture",
            subCategory = "Irrigation",
            description = "High-flow petrol pump for farm irrigation.",
            pricePerDay = 35000.0,
            imageUrl = "pump_url"
        ),
        Equipment(
            id = "4",
            name = "Knapsack Sprayer",
            category = "Agriculture",
            subCategory = "Sprayers",
            description = "20L manual sprayer for pest control.",
            pricePerDay = 15000.0,
            imageUrl = "sprayer_url"
        ),
        Equipment(
            id = "5",
            name = "Combine Harvester",
            category = "Agriculture",
            subCategory = "Harvesters",
            description = "Efficient harvester for maize and rice.",
            pricePerDay = 500000.0,
            imageUrl = "harvester_url"
        ),

        // Construction
        Equipment(
            id = "6",
            name = "Caterpillar 320 GC",
            category = "Construction",
            subCategory = "Excavators",
            description = "Reliable excavator for digging and lifting projects.",
            pricePerDay = 450000.0,
            imageUrl = "excavator_url"
        ),
        Equipment(
            id = "7",
            name = "Concrete Mixer",
            category = "Construction",
            subCategory = "Concrete Mixers",
            description = "Petrol powered mixer for construction sites.",
            pricePerDay = 85000.0,
            imageUrl = "mixer_url"
        ),
        Equipment(
            id = "8",
            name = "Scaffolding Set",
            category = "Construction",
            subCategory = "Scaffolding",
            description = "Complete set for high-reach work.",
            pricePerDay = 25000.0,
            imageUrl = "scaffolding_url"
        ),

        // Transport
        Equipment(
            id = "9",
            name = "Tata 407 Pickup",
            category = "Transport",
            subCategory = "Pickups",
            description = "Versatile pickup for moving community goods.",
            pricePerDay = 125000.0,
            imageUrl = "pickup_url"
        ),
        Equipment(
            id = "10",
            name = "Bajaj Boxer 150",
            category = "Transport",
            subCategory = "Motorcycles",
            description = "Rugged motorcycle for quick deliveries.",
            pricePerDay = 25000.0,
            imageUrl = "motorcycle_url"
        ),
        Equipment(
            id = "11",
            name = "Cold Storage Van",
            category = "Transport",
            subCategory = "Vans",
            description = "Refrigerated van for perishable goods.",
            pricePerDay = 200000.0,
            imageUrl = "van_url"
        ),

        // Community
        Equipment(
            id = "12",
            name = "Event Sound System",
            category = "Community",
            subCategory = "Sound",
            description = "Complete PA system with 2 speakers and mixer.",
            pricePerDay = 95000.0,
            imageUrl = "sound_url"
        ),
        Equipment(
            id = "13",
            name = "Community Tents",
            category = "Community",
            subCategory = "Tents",
            description = "Large 100-seater tent for gatherings.",
            pricePerDay = 65000.0,
            imageUrl = "tent_url"
        ),
        Equipment(
            id = "14",
            name = "Solar Home Kit",
            category = "Community",
            subCategory = "Solar Kits",
            description = "Lighting and charging solution for rural homes.",
            pricePerDay = 10000.0,
            imageUrl = "solar_url"
        ),

        // Livestock
        Equipment(
            id = "15",
            name = "Milk Cooler",
            category = "Livestock",
            subCategory = "Milk Coolers",
            description = "500L cooling tank for dairy preservation.",
            pricePerDay = 150000.0,
            imageUrl = "cooler_url"
        ),
        Equipment(
            id = "16",
            name = "Fodder Chopper",
            category = "Livestock",
            subCategory = "Choppers",
            description = "Efficient silage and fodder preparation machine.",
            pricePerDay = 45000.0,
            imageUrl = "chopper_url"
        ),
        Equipment(
            id = "17",
            name = "Veterinary Kit",
            category = "Livestock",
            subCategory = "Vet Kits",
            description = "Standard emergency equipment for livestock care.",
            pricePerDay = 20000.0,
            imageUrl = "vet_url"
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
