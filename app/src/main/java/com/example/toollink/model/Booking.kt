package com.example.toollink.model

import java.util.Date

data class Booking(
    val id: String,
    val toolName: String,
    val toolImageRes: Int? = null,
    val bookingDate: String,
    val amount: Double,
    val status: BookingStatus,
    val duration: String
)

enum class BookingStatus {
    COMPLETED,
    PENDING,
    CANCELLED
}
