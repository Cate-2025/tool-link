package com.example.toollink.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.toollink.model.Booking
import com.example.toollink.model.BookingStatus
import com.example.toollink.ui.theme.ToolLinkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingHistoryScreen() {
    val mockBookings = listOf(
        Booking("1", "Electric Drill", null, "2023-10-25", 25.0, BookingStatus.COMPLETED, "2 days"),
        Booking("2", "Lawn Mower", null, "2023-10-20", 45.0, BookingStatus.COMPLETED, "1 day"),
        Booking("3", "Pressure Washer", null, "2023-10-15", 30.0, BookingStatus.CANCELLED, "3 days"),
        Booking("4", "Ladder", null, "2023-10-10", 15.0, BookingStatus.COMPLETED, "5 days")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking History") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(mockBookings) { booking ->
                BookingItem(booking)
            }
        }
    }
}

@Composable
fun BookingItem(booking: Booking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.toolName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(booking.status)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Date: ${booking.bookingDate}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Duration: ${booking.duration}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$${booking.amount}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StatusBadge(status: BookingStatus) {
    val color = when (status) {
        BookingStatus.COMPLETED -> Color(0xFF4CAF50)
        BookingStatus.PENDING -> Color(0xFFFFC107)
        BookingStatus.CANCELLED -> Color(0xFFF44336)
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BookingHistoryPreview() {
    ToolLinkTheme {
        BookingHistoryScreen()
    }
}
