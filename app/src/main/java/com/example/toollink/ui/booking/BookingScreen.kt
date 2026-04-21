package com.example.toollink.ui.booking

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.toollink.ui.theme.ToolLinkTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

enum class BookingStep {
    TOOL_DETAIL, BOOKING_FORM, IDENTITY_VERIFICATION, REVIEW_SUMMARY, PAYMENT, PIN_ENTRY, PROCESSING, SUCCESS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    toolName: String = "Tool Link Tractor",
    pricePerHour: Double = 50.0
) {
    var currentStep by remember { mutableStateOf(BookingStep.TOOL_DETAIL) }
    
    // Booking State
    var location by remember { mutableStateOf("") }
    var specialInstructions by remember { mutableStateOf("") }
    var agreeToTerms by remember { mutableStateOf(false) }
    var durationHours by remember { mutableFloatStateOf(4f) }
    var selectedDate by remember { mutableLongStateOf(Calendar.getInstance().timeInMillis) }
    var selectedHour by remember { mutableIntStateOf(10) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    
    // Advanced Features
    var isProtectionEnabled by remember { mutableStateOf(true) }
    var isIdentityVerified by remember { mutableStateOf(false) }
    var promoCode by remember { mutableStateOf("") }
    val bookingId = remember { "TL-${(1000..9999).random()}" }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Payment State
    var paymentMethod by remember { mutableStateOf("Mobile Money") }
    var phoneNumber by remember { mutableStateOf("") }
    var mtnPin by remember { mutableStateOf("") }
    
    // Card State
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    // Price Calculations
    val basePrice = pricePerHour * durationHours.toInt()
    val protectionFee = if (isProtectionEnabled) 15.0 else 0.0
    val serviceFee = 5.0
    val totalPrice = basePrice + protectionFee + serviceFee
    
    val dateFormatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
    val timeFormatter = String.format("%02d:%02d", selectedHour, selectedMinute)

    // Form Validation logic
    val isFormValid = location.isNotBlank() && agreeToTerms
    val isPaymentValid = if (paymentMethod == "Mobile Money") {
        phoneNumber.length >= 10
    } else {
        cardNumber.length >= 16 && expiryDate.length >= 4 && cvv.length >= 3
    }

    LaunchedEffect(currentStep) {
        if (currentStep == BookingStep.PROCESSING) {
            delay(2000) 
            currentStep = BookingStep.SUCCESS
        }
    }

    AnimatedContent(
        targetState = currentStep,
        transitionSpec = {
            slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
        },
        label = "BookingFlow"
    ) { step ->
        when (step) {
            BookingStep.TOOL_DETAIL -> {
                ToolDetailContent(
                    name = toolName,
                    price = pricePerHour,
                    onStartBooking = { currentStep = BookingStep.BOOKING_FORM }
                )
            }
            BookingStep.BOOKING_FORM -> {
                BookingFormContent(
                    toolName = toolName,
                    location = location,
                    onLocationChange = { location = it },
                    instructions = specialInstructions,
                    onInstructionsChange = { specialInstructions = it },
                    agree = agreeToTerms,
                    onAgreeChange = { agreeToTerms = it },
                    duration = durationHours,
                    onDurationChange = { durationHours = it },
                    isProtectionEnabled = isProtectionEnabled,
                    onProtectionToggle = { isProtectionEnabled = it },
                    dateText = dateFormatter.format(Date(selectedDate)),
                    timeText = timeFormatter,
                    onDateClick = { showDatePicker = true },
                    onTimeClick = { showTimePicker = true },
                    onBack = { currentStep = BookingStep.TOOL_DETAIL },
                    onProceed = { currentStep = BookingStep.IDENTITY_VERIFICATION },
                    isFormValid = isFormValid
                )
            }
            BookingStep.IDENTITY_VERIFICATION -> {
                IdentityVerificationScreen(
                    onVerified = { 
                        isIdentityVerified = true
                        currentStep = BookingStep.REVIEW_SUMMARY 
                    },
                    onBack = { currentStep = BookingStep.BOOKING_FORM }
                )
            }
            BookingStep.REVIEW_SUMMARY -> {
                ReviewSummaryContent(
                    toolName = toolName,
                    location = location,
                    instructions = specialInstructions,
                    date = dateFormatter.format(Date(selectedDate)),
                    time = timeFormatter,
                    duration = durationHours.toInt(),
                    basePrice = basePrice,
                    protectionFee = protectionFee,
                    serviceFee = serviceFee,
                    total = totalPrice,
                    promoCode = promoCode,
                    onPromoCodeChange = { promoCode = it },
                    onBack = { currentStep = BookingStep.IDENTITY_VERIFICATION },
                    onConfirm = { currentStep = BookingStep.PAYMENT }
                )
            }
            BookingStep.PAYMENT -> {
                PaymentContent(
                    total = totalPrice,
                    method = paymentMethod,
                    onMethodChange = { paymentMethod = it },
                    phone = phoneNumber,
                    onPhoneChange = { phoneNumber = it },
                    cardNumber = cardNumber,
                    onCardNumberChange = { cardNumber = it },
                    expiry = expiryDate,
                    onExpiryChange = { expiryDate = it },
                    cvv = cvv,
                    onCvvChange = { cvv = it },
                    onBack = { currentStep = BookingStep.REVIEW_SUMMARY },
                    onConfirm = { 
                        if (paymentMethod == "Mobile Money") {
                            currentStep = BookingStep.PIN_ENTRY
                        } else {
                            currentStep = BookingStep.PROCESSING
                        }
                    },
                    isPaymentValid = isPaymentValid
                )
            }
            BookingStep.PIN_ENTRY -> {
                MobileMoneyPinScreen(
                    phone = phoneNumber,
                    total = totalPrice,
                    pin = mtnPin,
                    onPinChange = { mtnPin = it },
                    onConfirm = { currentStep = BookingStep.PROCESSING },
                    onBack = { currentStep = BookingStep.PAYMENT }
                )
            }
            BookingStep.PROCESSING -> {
                ProcessingPaymentScreen()
            }
            BookingStep.SUCCESS -> {
                BookingSuccessScreen(
                    id = bookingId,
                    onBack = { 
                        currentStep = BookingStep.TOOL_DETAIL 
                        location = ""
                        specialInstructions = ""
                        agreeToTerms = false
                        phoneNumber = ""
                        mtnPin = ""
                        cardNumber = ""
                        expiryDate = ""
                        cvv = ""
                    }
                )
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDate = datePickerState.selectedDateMillis ?: selectedDate
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedHour,
            initialMinute = selectedMinute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedHour = timePickerState.hour
                    selectedMinute = timePickerState.minute
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

@Composable
fun ToolDetailContent(name: String, price: Double, onStartBooking: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.height(250.dp).fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer)) {
            Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(80.dp).align(Alignment.Center), tint = MaterialTheme.colorScheme.onSecondaryContainer)
            Surface(
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Tool Link Verified", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color.White, fontSize = 12.sp)
            }
        }
        
        Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
            Text(text = name, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = "$$price per hour", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(20.dp))
                Text(" 4.9 (120 reviews)", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(20.dp))
                Text(" 3.2 km", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "About this Equipment", fontWeight = FontWeight.Bold)
            Text(
                text = "This equipment is highly rated for reliability and performance. Perfect for agricultural and construction projects. All tools on Tool Link are inspected regularly to ensure they meet our high quality standards.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Button(
                onClick = onStartBooking,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Start Booking Process", fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFormContent(
    toolName: String, location: String, onLocationChange: (String) -> Unit,
    instructions: String, onInstructionsChange: (String) -> Unit,
    agree: Boolean, onAgreeChange: (Boolean) -> Unit, duration: Float, onDurationChange: (Float) -> Unit,
    isProtectionEnabled: Boolean, onProtectionToggle: (Boolean) -> Unit,
    dateText: String, timeText: String, onDateClick: () -> Unit, onTimeClick: () -> Unit,
    onBack: () -> Unit, onProceed: () -> Unit, isFormValid: Boolean
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rental Form") }, 
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(20.dp).verticalScroll(rememberScrollState())) {
            Text("Complete the form to book $toolName", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))
            
            OutlinedTextField(
                value = location,
                onValueChange = onLocationChange,
                label = { Text("Delivery Address") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.LocationOn, null) }
            )
            
            Spacer(Modifier.height(16.dp))
            
            OutlinedTextField(
                value = instructions,
                onValueChange = onInstructionsChange,
                label = { Text("Special Instructions") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Near the main gate...") },
                minLines = 2
            )
            
            Spacer(Modifier.height(24.dp))
            
            Text("Pick-up Schedule", fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedCard(onClick = onDateClick, modifier = Modifier.weight(1f)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Date", style = MaterialTheme.typography.labelSmall)
                        Text(dateText, fontWeight = FontWeight.Bold)
                    }
                }
                OutlinedCard(onClick = onTimeClick, modifier = Modifier.weight(1f)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Time", style = MaterialTheme.typography.labelSmall)
                        Text(timeText, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(Modifier.height(24.dp))
            
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Duration", fontWeight = FontWeight.Bold)
                Text("${duration.toInt()} hours", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Slider(value = duration, onValueChange = onDurationChange, valueRange = 1f..48f, steps = 47)
            
            Spacer(Modifier.height(16.dp))
            
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Equipment Protection", fontWeight = FontWeight.Bold)
                        Text("Covers accidental damage during rental", style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(checked = isProtectionEnabled, onCheckedChange = onProtectionToggle)
                }
            }
            
            Spacer(Modifier.height(20.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = agree, onCheckedChange = onAgreeChange)
                Text("I agree to the Tool Link usage guidelines.", style = MaterialTheme.typography.bodySmall)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onProceed,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = isFormValid
            ) {
                Text("Continue to Verification")
            }
        }
    }
}

@Composable
fun IdentityVerificationScreen(onVerified: () -> Unit, onBack: () -> Unit) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.AccountBox, contentDescription = null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(24.dp))
        Text("Verify Identity", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "For security, Tool Link requires identity verification for heavy equipment rentals.",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(Modifier.height(40.dp))
        
        OutlinedCard(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            onClick = { galleryLauncher.launch("image/*") }
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Selected ID Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { imageUri = null },
                        modifier = Modifier.align(Alignment.TopEnd).background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Clear image", tint = Color.White)
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(32.dp))
                        Text("Upload Photo of National ID", style = MaterialTheme.typography.labelLarge)
                        Text("JPG, PNG up to 5MB", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
            }
        }
        
        Spacer(Modifier.height(48.dp))
        
        Button(
            onClick = onVerified, 
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = imageUri != null
        ) {
            Text("Verify & Continue")
        }
        TextButton(onClick = onBack) {
            Text("Go Back")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewSummaryContent(
    toolName: String, location: String, instructions: String, date: String, time: String,
    duration: Int, basePrice: Double, protectionFee: Double, serviceFee: Double, total: Double,
    promoCode: String, onPromoCodeChange: (String) -> Unit,
    onBack: () -> Unit, onConfirm: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Summary") }, 
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(20.dp).fillMaxSize().verticalScroll(rememberScrollState())) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(Modifier.padding(20.dp)) {
                    Text("Rental Summary", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(16.dp))
                    ReviewRow(label = "Equipment", value = toolName)
                    ReviewRow(label = "Location", value = location)
                    if (instructions.isNotBlank()) ReviewRow(label = "Note", value = instructions)
                    ReviewRow(label = "Schedule", value = "$date at $time")
                    ReviewRow(label = "Duration", value = "$duration hours")
                    
                    HorizontalDivider(Modifier.padding(vertical = 12.dp))
                    
                    ReviewRow(label = "Base Rental ($duration hrs)", value = "$$basePrice")
                    if (protectionFee > 0) ReviewRow(label = "Protection Plan", value = "$$protectionFee")
                    ReviewRow(label = "Service Fee", value = "$$serviceFee")
                    
                    HorizontalDivider(Modifier.padding(vertical = 12.dp))
                    
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Amount", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("$$total", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            
            Spacer(Modifier.height(24.dp))
            
            OutlinedTextField(
                value = promoCode,
                onValueChange = onPromoCodeChange,
                label = { Text("Promo Code (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { TextButton(onClick = {}) { Text("Apply") } }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(onClick = onConfirm, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text("Confirm & Proceed to Payment")
            }
        }
    }
}

@Composable
fun ReviewRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Medium, textAlign = TextAlign.End, modifier = Modifier.weight(1f).padding(start = 16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentContent(
    total: Double, method: String, onMethodChange: (String) -> Unit,
    phone: String, onPhoneChange: (String) -> Unit,
    cardNumber: String, onCardNumberChange: (String) -> Unit,
    expiry: String, onExpiryChange: (String) -> Unit,
    cvv: String, onCvvChange: (String) -> Unit,
    onBack: () -> Unit, onConfirm: () -> Unit, isPaymentValid: Boolean
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment Method") }, 
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(20.dp).verticalScroll(rememberScrollState())) {
            Text("Select how you want to pay", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))
            
            PaymentMethodCard(
                title = "Mobile Money",
                icon = Icons.Default.Phone,
                selected = method == "Mobile Money",
                onClick = { onMethodChange("Mobile Money") }
            )
            Spacer(Modifier.height(12.dp))
            PaymentMethodCard(
                title = "Credit / Debit Card",
                icon = Icons.Default.AccountBox,
                selected = method == "Card",
                onClick = { onMethodChange("Card") }
            )
            
            Spacer(Modifier.height(32.dp))
            
            if (method == "Mobile Money") {
                OutlinedTextField(
                    value = phone,
                    onValueChange = onPhoneChange,
                    label = { Text("Mobile Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    leadingIcon = { Icon(Icons.Default.Call, null) }
                )
                Spacer(Modifier.height(8.dp))
                Text("You will be asked for your PIN in the next step.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            } else {
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = onCardNumberChange,
                    label = { Text("Card Number") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Icon(Icons.Default.AccountBox, null) }
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = expiry,
                        onValueChange = onExpiryChange,
                        label = { Text("Expiry (MM/YY)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = cvv,
                        onValueChange = onCvvChange,
                        label = { Text("CVV") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
            
            Spacer(Modifier.height(40.dp))
            
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                enabled = isPaymentValid
            ) {
                Text(if (method == "Mobile Money") "Continue to PIN" else "Pay $$total Now")
            }
        }
    }
}

@Composable
fun MobileMoneyPinScreen(
    phone: String, total: Double, pin: String, onPinChange: (String) -> Unit, 
    onConfirm: () -> Unit, onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(24.dp))
        Text("Authorize Payment", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Paying $$total from $phone", style = MaterialTheme.typography.bodyMedium)
        
        Spacer(Modifier.height(32.dp))
        
        OutlinedTextField(
            value = pin,
            onValueChange = onPinChange,
            label = { Text("Enter MM PIN") },
            modifier = Modifier.width(200.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            textStyle = TextStyle(textAlign = TextAlign.Center)
        )
        
        Spacer(Modifier.height(40.dp))
        
        Button(
            onClick = onConfirm, 
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = pin.length >= 4
        ) {
            Text("Confirm Payment")
        }
        TextButton(onClick = onBack) {
            Text("Cancel")
        }
    }
}

@Composable
fun PaymentMethodCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, onClick: () -> Unit) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        border = if (selected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.width(16.dp))
            Text(title, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
            Spacer(Modifier.weight(1f))
            RadioButton(selected = selected, onClick = onClick)
        }
    }
}

@Composable
fun ProcessingPaymentScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(64.dp), color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(32.dp))
        Text("Processing Payment...", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Validating with your provider", color = Color.Gray)
    }
}

@Composable
fun BookingSuccessScreen(id: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(120.dp), tint = Color(0xFF4CAF50))
        Spacer(Modifier.height(24.dp))
        Text("Payment Successful!", fontSize = 28.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text("Booking ID: $id", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        Text(
            "Your order has been confirmed. You can track your equipment status in the My Bookings tab.",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(60.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text("Back to Tool Details")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookingPreview() {
    ToolLinkTheme { BookingScreen() }
}
