package com.example.toollink.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.toollink.ui.theme.ToolLinkTheme

data class BookingRecord(
    val id: String,
    val toolName: String,
    val date: String,
    val status: String,
    val amount: Double,
    val location: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(onBack: () -> Unit = {}) {
    val mockBookings = listOf(
        BookingRecord("TL-9021", "Tool Link Tractor", "24 Oct 2023", "Active", 150.0, "Central Farm"),
        BookingRecord("TL-8842", "Water Pump Kit", "15 Oct 2023", "Completed", 45.0, "North Creek"),
        BookingRecord("TL-7731", "Concrete Mixer", "02 Oct 2023", "Completed", 80.0, "Building Site A"),
        BookingRecord("TL-6540", "Solar Generator", "28 Sep 2023", "Cancelled", 30.0, "Community Hall")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Bookings", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(mockBookings) { booking ->
                BookingHistoryItem(booking)
            }
        }
    }
}

@Composable
fun BookingHistoryItem(booking: BookingRecord) {
    val statusColor = when (booking.status) {
        "Active" -> MaterialTheme.colorScheme.primary
        "Completed" -> Color(0xFF4CAF50)
        "Cancelled" -> Color.Red
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = booking.id, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = booking.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = booking.toolName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = booking.date, style = MaterialTheme.typography.bodyMedium)
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = booking.location, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Amount Paid", style = MaterialTheme.typography.bodySmall)
                Text(text = "$${booking.amount}", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = { /* View Details */ },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Details")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryPreview() {
    ToolLinkTheme {
        HistoryScreen()
    }
}
