package com.example.toollink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.toollink.ui.booking.BookingScreen
import com.example.toollink.ui.owner.OwnerRegistrationScreen
import com.example.toollink.ui.screens.BookingHistoryScreen
import com.example.toollink.ui.theme.ToolLinkTheme
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat
import java.util.Locale

// --- MAIN ACTIVITY ---

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToolLinkTheme {
                // Initialize based on actual Firebase Auth state
                val auth = remember { FirebaseAuth.getInstance() }
                var isAuthenticated by remember { mutableStateOf(auth.currentUser != null) }

                var currentMainTab by remember { mutableStateOf("marketplace") }
                var selectedEquipmentForBooking by remember { mutableStateOf<Equipment?>(null) }

                if (!isAuthenticated) {
                    // This shows the AuthScreen if not authenticated
                    AuthScreen(onAuthSuccess = { isAuthenticated = true })
                } else {
                    Scaffold(
                        topBar = {
                            if (selectedEquipmentForBooking == null) {
                                CenterAlignedTopAppBar(
                                    title = { Text("Tool Link", fontWeight = FontWeight.Bold) },
                                    actions = {
                                        // Added Logout button to allow getting back to login screen
                                        IconButton(onClick = {
                                            auth.signOut()
                                            isAuthenticated = false
                                        }) {
                                            Icon(Icons.Default.Logout, contentDescription = "Sign Out")
                                        }
                                    }
                                )
                            }
                        },
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
                                    BookingScreen(
                                        toolName = selectedEquipmentForBooking!!.name,
                                        pricePerHour = selectedEquipmentForBooking!!.pricePerDay / 8
                                    )
                                    
                                    IconButton(
                                        onClick = { selectedEquipmentForBooking = null },
                                        modifier = Modifier.padding(8.dp).statusBarsPadding().align(Alignment.TopStart)
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
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf(
        Category("Agriculture", Icons.Default.Agriculture, Color(0xFF2E7D32)),
        Category("Construction", Icons.Default.Build, Color(0xFFEF6C00)),
        Category("Transport", Icons.Default.LocalShipping, Color(0xFF1565C0)),
        Category("Community", Icons.Default.Groups, Color(0xFF6A1B9A)),
        Category("Livestock", Icons.Default.Pets, Color(0xFFC62828))
    )

    var selectedIndex by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Filter
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search tools or locations...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, null)
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = categories[selectedIndex].themeColor,
                cursorColor = categories[selectedIndex].themeColor
            )
        )

        ScrollableTabRow(
            selectedTabIndex = selectedIndex, 
            edgePadding = 16.dp, 
            containerColor = Color.Transparent,
            divider = {},
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    color = categories[selectedIndex].themeColor
                )
            }
        ) {
            categories.forEachIndexed { index, category ->
                Tab(
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                    text = { 
                        Text(
                            category.label,
                            color = if (selectedIndex == index) category.themeColor else Color.Gray,
                            fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal
                        ) 
                    },
                    icon = {
                        Icon(
                            category.icon,
                            contentDescription = null,
                            tint = if (selectedIndex == index) category.themeColor else Color.Gray
                        )
                    }
                )
            }
        }

        CategoryDetailScreen(categories[selectedIndex], searchQuery, onBookNow)
    }
}

@Composable
fun CategoryDetailScreen(category: Category, searchQuery: String, onBookNow: (Equipment) -> Unit) {
    val repository = remember { EquipmentRepository() }
    var equipmentList by remember { mutableStateOf(emptyList<Equipment>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(category, searchQuery) {
        isLoading = true
        val result = repository.getEquipmentByCategory(category.label)
        equipmentList = if (searchQuery.isEmpty()) {
            result
        } else {
            result.filter { 
                it.name.contains(searchQuery, ignoreCase = true) || 
                it.location.contains(searchQuery, ignoreCase = true) ||
                it.subCategory.contains(searchQuery, ignoreCase = true)
            }
        }
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = category.themeColor)
        }
    } else if (equipmentList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No equipment found", color = Color.Gray)
        }
    } else {
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
}

@Composable
fun EquipmentListItem(
    equipment: Equipment,
    themeColor: Color,
    onBookNow: (Equipment) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBookNow(equipment) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = equipment.imageUrl,
                    contentDescription = equipment.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                
                Surface(
                    modifier = Modifier.padding(12.dp),
                    color = if (equipment.isAvailable) Color(0xFF4CAF50) else Color.Red,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (equipment.isAvailable) "Available" else "Rented",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = equipment.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = equipment.subCategory,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "UGX ${NumberFormat.getNumberInstance(Locale.US).format(equipment.pricePerDay)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = themeColor,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "per day",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = themeColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = equipment.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { onBookNow(equipment) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                    shape = RoundedCornerShape(8.dp),
                    enabled = equipment.isAvailable
                ) {
                    Icon(Icons.Default.Event, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Book Now")
                }
            }
        }
    }
}
