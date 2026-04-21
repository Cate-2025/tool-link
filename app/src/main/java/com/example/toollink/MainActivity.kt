package com.example.toollink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.toollink.ui.screens.BookingHistoryScreen
import com.example.toollink.ui.screens.PaymentScreen
import com.example.toollink.ui.theme.ToolLinkTheme
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToolLinkTheme {
                var isAuthenticated by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser != null) }
                var currentMainTab by remember { mutableStateOf("marketplace") }
                var selectedEquipmentForPayment by remember { mutableStateOf<Equipment?>(null) }

                if (!isAuthenticated) {
                    AuthScreen(onAuthSuccess = { isAuthenticated = true })
                } else {
                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentMainTab == "marketplace",
                                    onClick = { 
                                        currentMainTab = "marketplace"
                                        selectedEquipmentForPayment = null
                                    },
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                    label = { Text("Home") }
                                )
                                NavigationBarItem(
                                    selected = currentMainTab == "history",
                                    onClick = { 
                                        currentMainTab = "history"
                                        selectedEquipmentForPayment = null
                                    },
                                    icon = { Icon(Icons.Default.History, contentDescription = "History") },
                                    label = { Text("History") }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            when {
                                selectedEquipmentForPayment != null -> {
                                    PaymentScreen(onPaymentSuccess = {
                                        selectedEquipmentForPayment = null
                                        currentMainTab = "history"
                                    })
                                }
                                currentMainTab == "marketplace" -> {
                                    ToolLinkApp(onBookNow = { equipment ->
                                        selectedEquipmentForPayment = equipment
                                    })
                                }
                                currentMainTab == "history" -> {
                                    BookingHistoryScreen()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class Category(
    val label: String,
    val icon: ImageVector,
    val subCategories: List<String>,
    val description: String,
    val themeColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolLinkApp(onBookNow: (Equipment) -> Unit) {
    val categories = listOf(
        Category(
            "Agriculture", Icons.Default.Agriculture,
            listOf("Tractors", "Ploughs", "Harvesters", "Irrigation", "Sprayers"),
            "Modern machinery for high-yield farming.",
            Color(0xFF2E7D32)
        ),
        Category(
            "Construction", Icons.Default.Build,
            listOf("Excavators", "Bulldozers", "Mixers", "Scaffolding", "Cranes"),
            "Heavy-duty tools for infrastructure.",
            Color(0xFFEF6C00)
        ),
        Category(
            "Transport", Icons.Default.LocalShipping,
            listOf("Trailers", "Pickups", "Motorcycles", "Cold Storage", "Vans"),
            "Reliable vehicles for logistics.",
            Color(0xFF1565C0)
        ),
        Category(
            "Community", Icons.Default.Groups,
            listOf("Tents", "Chairs", "Sound", "Water Tanks", "Solar Kits"),
            "Shared resources for events & projects.",
            Color(0xFF6A1B9A)
        ),
        Category(
            "Livestock", Icons.Default.Pets,
            listOf("Milk Coolers", "Choppers", "Vet Kits", "Feeders", "Shearers"),
            "Expert support for your livestock.",
            Color(0xFFAD1457)
        )
    )

    var selectedIndex by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            categories.forEachIndexed { index, category ->
                val isSelected = selectedIndex == index
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { selectedIndex = index },
                    icon = {
                        Icon(
                            category.icon,
                            contentDescription = category.label,
                            tint = if (isSelected) category.themeColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    label = {
                        Text(
                            category.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = category.themeColor.copy(alpha = 0.1f)
                    )
                )
            }
        }

        AnimatedContent(
            targetState = selectedIndex,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally { it } + fadeIn()) togetherWith (slideOutHorizontally { -it } + fadeOut())
                } else {
                    (slideInHorizontally { -it } + fadeIn()) togetherWith (slideOutHorizontally { it } + fadeOut())
                }
            },
            modifier = Modifier.weight(1f),
            label = "CategoryTransition"
        ) { index ->
            CategoryDetailScreen(categories[index], onBookNow)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(category: Category, onBookNow: (Equipment) -> Unit) {
    val repository = remember { EquipmentRepository() }
    val equipmentList = remember { mutableStateListOf<Equipment>() }
    
    var showFilters by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf<String?>(null) }
    var maxPrice by remember { mutableStateOf<String?>(null) }
    
    val locations = listOf("Kampala", "Mbarara", "Gulu", "Jinja", "Mbale", "Entebbe", "Wakiso", "Masaka", "Mukono")

    LaunchedEffect(category.label, selectedLocation, maxPrice) {
        equipmentList.clear()
        val filtered = repository.filterEquipment(
            category = category.label,
            location = selectedLocation,
            maxPrice = maxPrice?.toDoubleOrNull()
        )
        equipmentList.addAll(filtered)
    }

    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Hero Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(category.themeColor, category.themeColor.copy(alpha = 0.7f))
                    )
                )
                .padding(24.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Column {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = category.label,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )
            }
        }

        // Filter Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${equipmentList.size} items found",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
            
            Surface(
                onClick = { showFilters = true },
                shape = RoundedCornerShape(12.dp),
                color = category.themeColor.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = category.themeColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Filters",
                        color = category.themeColor,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }

        // Active Filter Chips
        if (selectedLocation != null || maxPrice != null) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                if (selectedLocation != null) {
                    item {
                        FilterChip(
                            selected = true,
                            onClick = { selectedLocation = null },
                            label = { Text(selectedLocation ?: "") }
                        )
                    }
                }
                if (maxPrice != null) {
                    item {
                        FilterChip(
                            selected = true,
                            onClick = { maxPrice = null },
                            label = { Text("Max: UGX ${maxPrice ?: ""}") }
                        )
                    }
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(equipmentList) { equipment ->
                EquipmentListItem(equipment, category.themeColor, onBookNow)
            }
        }
    }

    if (showFilters) {
        ModalBottomSheet(
            onDismissRequest = { showFilters = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    "Filter Equipment",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Location", style = MaterialTheme.typography.titleMedium)
                LazyRow(
                    modifier = Modifier.padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(locations) { loc ->
                        FilterChip(
                            selected = selectedLocation == loc,
                            onClick = { selectedLocation = if (selectedLocation == loc) null else loc },
                            label = { Text(loc) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Max Price (UGX)", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = maxPrice ?: "",
                    onValueChange = { maxPrice = it },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    placeholder = { Text("e.g. 100000") },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

@Composable
fun EquipmentListItem(equipment: Equipment, color: Color, onBookNow: (Equipment) -> Unit) {
    val formattedPrice = "UGX " + NumberFormat.getNumberInstance(Locale.US).format(equipment.pricePerDay)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onBookNow(equipment) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Equipment Image using Coil
            AsyncImage(
                model = equipment.imageUrl,
                contentDescription = equipment.name,
                modifier = Modifier
                    .width(130.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = equipment.subCategory,
                            style = MaterialTheme.typography.labelSmall,
                            color = color
                        )
                        Surface(
                            color = if (equipment.isAvailable) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (equipment.isAvailable) "Available" else "Rented",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (equipment.isAvailable) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                        }
                    }
                    
                    Text(
                        text = equipment.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = equipment.location,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = formattedPrice,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "/ day",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
