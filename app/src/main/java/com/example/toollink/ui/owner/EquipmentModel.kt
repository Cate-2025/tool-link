package com.example.toollink.ui.owner

import android.net.Uri

data class OwnerRegistration(
    // Personal & Address
    val legalName: String = "",
    val proofOfIdentityUri: Uri? = null,
    val proofOfAddressUri: Uri? = null,
    
    // Vehicle Documents
    val vehicleRegistrationUri: Uri? = null,
    val vehicleInsuranceUri: Uri? = null,
    val operatorsLicenseUri: Uri? = null,
    
    // Vehicle Details
    val make: String = "",
    val model: String = "",
    val year: String = "",
    val vin: String = "",
    val licensePlate: String = "",
    val engineNumber: String = "",
    
    // Usage & Banking
    val purposeOfUse: String = "",
    val bankAccountName: String = "",
    val bankAccountNumber: String = "",
    val bankName: String = "",
    val backgroundCheckConsent: Boolean = false
)

data class Equipment(
    val id: String = "",
    val name: String,
    val description: String,
    val pricePerHour: Double,
    val availability: String,
    val imageUri: Uri? = null
)
