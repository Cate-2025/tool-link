package com.example.toollink

import kotlinx.coroutines.delay

class EquipmentRepository {
    private val mockEquipment = mutableListOf(
        // Agriculture
        Equipment(
            id = "1",
            name = "John Deere 5050D",
            category = "Agriculture",
            subCategory = "Tractors",
            description = "50 HP tractor, perfect for medium scale farming and haulage.",
            pricePerDay = 150000.0,
            location = "Kampala",
            imageUrl = "https://images.unsplash.com/photo-1594913785162-e6785b426cb9?auto=format&fit=crop&q=80&w=400"
        ),
        Equipment(
            id = "2",
            name = "Disc Plough",
            category = "Agriculture",
            subCategory = "Ploughs",
            description = "3-disc plough for tough soil preparation.",
            pricePerDay = 45000.0,
            location = "Mbarara",
            imageUrl = "https://images.unsplash.com/photo-1592982537447-7440770cbfc9?auto=format&fit=crop&q=80&w=400"
        ),
        Equipment(
            id = "3",
            name = "Yamaha Water Pump",
            category = "Agriculture",
            subCategory = "Irrigation",
            description = "High-flow petrol pump for farm irrigation.",
            pricePerDay = 35000.0,
            location = "Gulu",
            imageUrl = "https://images.unsplash.com/photo-1615811361523-6bd03d7748e7?auto=format&fit=crop&q=80&w=400"
        ),
        Equipment(
            id = "4",
            name = "Knapsack Sprayer",
            category = "Agriculture",
            subCategory = "Sprayers",
            description = "20L manual sprayer for pest control.",
            pricePerDay = 15000.0,
            location = "Jinja",
            imageUrl = "https://images.unsplash.com/photo-1590682680695-43b964a3ae17?auto=format&fit=crop&q=80&w=400"
        ),

        // Construction
        Equipment(
            id = "6",
            name = "Caterpillar 320 GC",
            category = "Construction",
            subCategory = "Excavators",
            description = "Reliable excavator for digging and lifting projects.",
            pricePerDay = 450000.0,
            location = "Kampala",
            imageUrl = "https://images.unsplash.com/photo-1579412691511-27468b7ca28a?auto=format&fit=crop&q=80&w=400"
        ),
        Equipment(
            id = "7",
            name = "Concrete Mixer",
            category = "Construction",
            subCategory = "Concrete Mixers",
            description = "Petrol powered mixer for construction sites.",
            pricePerDay = 85000.0,
            location = "Entebbe",
            imageUrl = "https://images.unsplash.com/photo-1541888946425-d81bb19480c5?auto=format&fit=crop&q=80&w=400"
        ),

        // Transport
        Equipment(
            id = "9",
            name = "Tata 407 Pickup",
            category = "Transport",
            subCategory = "Pickups",
            description = "Versatile pickup for moving community goods.",
            pricePerDay = 125000.0,
            location = "Kampala",
            imageUrl = "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&q=80&w=400"
        ),
        Equipment(
            id = "10",
            name = "Bajaj Boxer 150",
            category = "Transport",
            subCategory = "Motorcycles",
            description = "Rugged motorcycle for quick deliveries.",
            pricePerDay = 25000.0,
            location = "Masaka",
            imageUrl = "https://images.unsplash.com/photo-1558981403-c5f91cbba527?auto=format&fit=crop&q=80&w=400"
        ),

        // Community
        Equipment(
            id = "12",
            name = "Event Sound System",
            category = "Community",
            subCategory = "Sound",
            description = "Complete PA system with 2 speakers and mixer.",
            pricePerDay = 95000.0,
            location = "Mukono",
            imageUrl = "https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&q=80&w=400"
        ),
        Equipment(
            id = "13",
            name = "Community Tents",
            category = "Community",
            subCategory = "Tents",
            description = "Large 100-seater tent for gatherings.",
            pricePerDay = 65000.0,
            location = "Iganga",
            imageUrl = "https://images.unsplash.com/photo-1519167758481-83f550bb49b3?auto=format&fit=crop&q=80&w=400"
        ),

        // Livestock
        Equipment(
            id = "15",
            name = "Milk Cooler",
            category = "Livestock",
            subCategory = "Milk Coolers",
            description = "500L cooling tank for dairy preservation.",
            pricePerDay = 150000.0,
            location = "Mbarara",
            imageUrl = "https://images.unsplash.com/photo-1559591937-e6b76852b78d?auto=format&fit=crop&q=80&w=400"
        ),
        Equipment(
            id = "16",
            name = "Fodder Chopper",
            category = "Livestock",
            subCategory = "Choppers",
            description = "Efficient silage and fodder preparation machine.",
            pricePerDay = 45000.0,
            location = "Bushenyi",
            imageUrl = "https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&q=80&w=400"
        ),
        Equipment(
            id = "17",
            name = "Veterinary Kit",
            category = "Livestock",
            subCategory = "Vet Kits",
            description = "Standard emergency equipment for livestock care.",
            pricePerDay = 20000.0,
            location = "Fort Portal",
            imageUrl = "https://images.unsplash.com/photo-1584036561566-baf8f5f1b144?auto=format&fit=crop&q=80&w=400"
        )
    )

    suspend fun getAllEquipment(): List<Equipment> {
        delay(300)
        return mockEquipment
    }

    suspend fun getEquipmentByCategory(category: String): List<Equipment> {
        delay(300)
        return mockEquipment.filter { it.category == category }
    }
    
    suspend fun filterEquipment(
        category: String,
        location: String? = null,
        maxPrice: Double? = null
    ): List<Equipment> {
        delay(300)
        return mockEquipment.filter { 
            it.category == category &&
            (location == null || it.location.contains(location, ignoreCase = true)) &&
            (maxPrice == null || it.pricePerDay <= maxPrice)
        }
    }

    suspend fun addEquipment(equipment: Equipment): Boolean {
        delay(300)
        val newId = (mockEquipment.size + 1).toString()
        mockEquipment.add(equipment.copy(id = newId))
        return true
    }
}
