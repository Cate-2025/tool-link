package com.example.toollink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.toollink.ui.booking.BookingScreen
import com.example.toollink.ui.owner.OwnerRegistrationScreen
import com.example.toollink.ui.screens.BookingHistoryScreen
import com.example.toollink.ui.screens.PaymentScreen
import com.example.toollink.ui.theme.ToolLinkTheme
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat
import java.util.Locale

// --- MOCK DATA MODELS FOR INTEGRATION ---
data class Equipment(
    val id: String,
    val name: String,
    val category: String,
    val subCategory: String,
    val pricePerDay: Double,
    val location: String,
    val imageUrl: String,
    val isAvailable: Boolean = true
)

class EquipmentRepository {
    private val equipment = listOf(
        Equipment("1", "Massey Ferguson 260", "Agriculture", "Tractors", 150000.0, "Mbarara", "https://example.com/tractor1.jpg"),
        Equipment("2", "Concrete Mixer 200L", "Construction", "Mixers", 80000.0, "Kampala", "https://example.com/mixer.jpg"),
        Equipment("3", "Flatbed Trailer", "Transport", "Trailers", 50000.0, "Jinja", "https://example.com/trailer.jpg")
    )

    fun filterEquipment(category: String, location: String?, maxPrice: Double?): List<Equipment> {
        return equipment.filter { 
            it.category == category && 
            (location == null || it.location == location) && 
            (maxPrice == null || it.pricePerDay <= maxPrice)
        }
    }
}

@Composable
fun AuthScreen(onAuthSuccess: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Tool Link Auth", style = MaterialTheme.typography.headlineMedium)
        Button(onClick = onAuthSuccess, modifier = Modifier.padding(top = 16.dp)) {
            Text("Mock Login")
        }
    }
}

// --- MAIN ACTIVITY ---

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToolLinkTheme {
                var isAuthenticated by remember { mutableStateOf(true) } // Set true for demo
                var currentMainTab by remember { mutableStateOf("marketplace") }
                var selectedEquipmentForBooking by remember { mutableStateOf<Equipment?>(null) }

                if (!isAuthenticated) {
                    AuthScreen(onAuthSuccess = { isAuthenticated = true })
                } else {
                    Scaffold(
                        bottomBar = {
                            if (selectedEquipmentForBooking == null) {
                                NavigationBar {
                                    NavigationBarItem(
                                        selected = currentMainTab == "marketplace",
                                        onClick = { currentMainTab = "marketplace" },
                                        icon = { Icon(Icons.Default.Home, null) },
                                        label = { Text("Home") }
                                    )
                                    NavigationBarItem(
                                        selected = currentMainTab == "history",
                                        onClick = { currentMainTab = "history" },
                                        icon = { Icon(Icons.Default.History, null) },
                                        label = { Text("History") }
                                    )
                                    NavigationBarItem(
                                        selected = currentMainTab == "owner",
                                        onClick = { currentMainTab = "owner" },
                                        icon = { Icon(Icons.Default.Person, null) },
                                        label = { Text("Owner") }
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            when {
                                selectedEquipmentForBooking != null -> {
                                    // INTEGRATION: This calls YOUR Booking Screen
                                    BookingScreen(
                                        toolName = selectedEquipmentForBooking!!.name,
                                        pricePerHour = selectedEquipmentForBooking!!.pricePerDay / 8 // Mock conversion
                                    )
                                    
                                    // Add a back button for the booking overlay
                                    IconButton(
                                        onClick = { selectedEquipmentForBooking = null },
                                        modifier = Modifier.padding(8.dp).align(Alignment.TopStart)
                                    ) {
                                        Icon(Icons.Default.Close, "Close Booking")
                                    }
                                }
                                currentMainTab == "marketplace" -> {
                                    ToolLinkApp(onBookNow = { equipment ->
                                        selectedEquipmentForBooking = equipment
                                    })
                                }
                                currentMainTab == "history" -> {
                                    BookingHistoryScreen()
                                }
                                currentMainTab == "owner" -> {
                                    OwnerRegistrationScreen()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- TEAM UI COMPONENTS (MARKETPLACE) ---

data class Category(
    val label: String,
    val icon: ImageVector,
    val themeColor: Color
)

@Composable
fun ToolLinkApp(onBookNow: (Equipment) -> Unit) {
    val categories = listOf(
        Category("Agriculture", Icons.Default.Agriculture, Color(0xFF2E7D32)),
        Category("Construction", Icons.Default.Build, Color(0xFFEF6C00)),
        Category("Transport", Icons.Default.LocalShipping, Color(0xFF1565C0))
    )

    var selectedIndex by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(selectedTabIndex = selectedIndex, edgePadding = 16.dp, containerColor = Color.Transparent) {
            categories.forEachIndexed { index, category ->
                Tab(
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                    text = { Text(category.label) }
                )
            }
        }

        CategoryDetailScreen(categories[selectedIndex], onBookNow)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(category: Category, onBookNow: (Equipment) -> Unit) {
    val repository = remember { EquipmentRepository() }
    val equipmentList = repository.filterEquipment(category.label, null, null)

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(equipmentList) { equipment ->
            EquipmentListItem(equipment, category.themeColor, onBookNow)
        }
    }
}

@Composable
fun EquipmentListItem(equipment: Equipment, color: Color, onBookNow: (Equipment) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onBookNow(equipment) },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(80.dp).background(color.copy(alpha = 0.2f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Settings, null, tint = color)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(equipment.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("UGX ${equipment.pricePerDay} / day", color = color)
                Text(equipment.location, style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = { onBookNow(equipment) }) {
                Text("Book")
            }
        }
    }
}
