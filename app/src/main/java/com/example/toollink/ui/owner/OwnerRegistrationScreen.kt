package com.example.toollink.ui.owner

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.toollink.ui.theme.ToolLinkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerRegistrationScreen(
    viewModel: OwnerRegistrationViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Launcher for various documents
    val identityLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { viewModel.proofOfIdentityUri = it }
    val addressLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { viewModel.proofOfAddressUri = it }
    val registrationLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { viewModel.vehicleRegistrationUri = it }
    val insuranceLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { viewModel.vehicleInsuranceUri = it }
    val licenseLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { viewModel.operatorsLicenseUri = it }

    LaunchedEffect(viewModel.uiState) {
        when (val state = viewModel.uiState) {
            is RegistrationUiState.Success -> {
                snackbarHostState.showSnackbar("Registration Submitted Successfully!")
                viewModel.resetState()
            }
            is RegistrationUiState.Error -> {
                snackbarHostState.showSnackbar("Error: ${state.message}")
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = { 
                        Text(
                            "Owner Registration",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall
                        ) 
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Complete your profile to start earning with Tool Link",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                RegistrationSectionCard(
                    title = "1. Personal Information",
                    icon = Icons.Outlined.Person
                ) {
                    OutlinedTextField(
                        value = viewModel.legalName,
                        onValueChange = { viewModel.onNameChange(it) },
                        label = { Text("Legal Name (as per ID)") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = viewModel.nameError != null,
                        supportingText = { viewModel.nameError?.let { Text(it) } },
                        shape = RoundedCornerShape(12.dp)
                    )

                    DocumentPickerItem("Proof of Identity", viewModel.proofOfIdentityUri) { identityLauncher.launch("image/*") }
                    DocumentPickerItem("Proof of Address", viewModel.proofOfAddressUri) { addressLauncher.launch("image/*") }
                }

                RegistrationSectionCard(
                    title = "2. Vehicle Documents",
                    icon = Icons.Outlined.Description
                ) {
                    DocumentPickerItem("Vehicle Registration", viewModel.vehicleRegistrationUri) { registrationLauncher.launch("application/pdf,image/*") }
                    DocumentPickerItem("Vehicle Insurance", viewModel.vehicleInsuranceUri) { insuranceLauncher.launch("application/pdf,image/*") }
                    DocumentPickerItem("Operator's License", viewModel.operatorsLicenseUri) { licenseLauncher.launch("application/pdf,image/*") }
                }

                RegistrationSectionCard(
                    title = "3. Vehicle Details",
                    icon = Icons.Outlined.Agriculture
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(value = viewModel.make, onValueChange = { viewModel.make = it }, label = { Text("Make") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                        OutlinedTextField(value = viewModel.model, onValueChange = { viewModel.model = it }, label = { Text("Model") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                    }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(value = viewModel.year, onValueChange = { viewModel.year = it }, label = { Text("Year") }, modifier = Modifier.weight(0.4f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), shape = RoundedCornerShape(12.dp))
                        OutlinedTextField(value = viewModel.licensePlate, onValueChange = { viewModel.licensePlate = it }, label = { Text("License Plate") }, modifier = Modifier.weight(0.6f), shape = RoundedCornerShape(12.dp))
                    }

                    OutlinedTextField(
                        value = viewModel.vin,
                        onValueChange = { viewModel.vin = it },
                        label = { Text("VIN (Chassis Number)") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = viewModel.vinError != null,
                        supportingText = { viewModel.vinError?.let { Text(it) } },
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    OutlinedTextField(value = viewModel.engineNumber, onValueChange = { viewModel.engineNumber = it }, label = { Text("Engine Number") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                }

                RegistrationSectionCard(
                    title = "4. Usage & Payments",
                    icon = Icons.Rounded.AccountBalance
                ) {
                    OutlinedTextField(
                        value = viewModel.purposeOfUse,
                        onValueChange = { viewModel.purposeOfUse = it },
                        label = { Text("Purpose of Use (e.g. Hauling)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Banking Information", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)

                    OutlinedTextField(value = viewModel.bankName, onValueChange = { viewModel.bankName = it }, label = { Text("Bank Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = viewModel.bankAccountName, onValueChange = { viewModel.bankAccountName = it }, label = { Text("Account Holder Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = viewModel.bankAccountNumber, onValueChange = { viewModel.bankAccountNumber = it }, label = { Text("Account Number") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), shape = RoundedCornerShape(12.dp))
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = viewModel.backgroundCheckConsent, onCheckedChange = { viewModel.backgroundCheckConsent = it })
                        Text(
                            "I consent to a background check for safety and compliance purposes.",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Button(
                    onClick = { viewModel.submitRegistration() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    enabled = viewModel.uiState !is RegistrationUiState.Loading,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (viewModel.uiState is RegistrationUiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 3.dp)
                    } else {
                        Text("SUBMIT FOR REVIEW", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, letterSpacing = 1.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun RegistrationSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            content()
        }
    }
}

@Composable
fun DocumentPickerItem(label: String, uri: Uri?, onClick: () -> Unit) {
    val backgroundColor by animateColorAsState(
        targetValue = if (uri != null) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        animationSpec = tween(durationMillis = 500)
    )
    val borderColor by animateColorAsState(
        targetValue = if (uri != null) Color(0xFF4CAF50) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
            Text(
                if (uri != null) "✓ Attached: ${uri.lastPathSegment}" else "Tap to upload file",
                style = MaterialTheme.typography.bodySmall,
                color = if (uri != null) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = if (uri != null) Icons.Rounded.TaskAlt else Icons.Rounded.CloudUpload,
            contentDescription = null,
            tint = if (uri != null) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FullRegistrationAttractivePreview() {
    ToolLinkTheme {
        OwnerRegistrationScreen()
    }
}
