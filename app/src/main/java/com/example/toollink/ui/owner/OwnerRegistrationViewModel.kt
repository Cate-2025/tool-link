package com.example.toollink.ui.owner

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

sealed class RegistrationUiState {
    object Idle : RegistrationUiState()
    object Loading : RegistrationUiState()
    object Success : RegistrationUiState()
    data class Error(val message: String) : RegistrationUiState()
}

class OwnerRegistrationViewModel(
    private val repository: EquipmentRepository = EquipmentRepositoryImpl()
) : ViewModel() {

    var uiState by mutableStateOf<RegistrationUiState>(RegistrationUiState.Idle)
        private set

    // Section 1: Personal & ID
    var legalName by mutableStateOf("")
    var proofOfIdentityUri by mutableStateOf<Uri?>(null)
    var proofOfAddressUri by mutableStateOf<Uri?>(null)

    // Section 2: Vehicle Documents
    var vehicleRegistrationUri by mutableStateOf<Uri?>(null)
    var vehicleInsuranceUri by mutableStateOf<Uri?>(null)
    var operatorsLicenseUri by mutableStateOf<Uri?>(null)

    // Section 3: Vehicle Details
    var make by mutableStateOf("")
    var model by mutableStateOf("")
    var year by mutableStateOf("")
    var vin by mutableStateOf("")
    var licensePlate by mutableStateOf("")
    var engineNumber by mutableStateOf("")

    // Section 4: Purpose & Banking
    var purposeOfUse by mutableStateOf("")
    var bankAccountName by mutableStateOf("")
    var bankAccountNumber by mutableStateOf("")
    var bankName by mutableStateOf("")
    var backgroundCheckConsent by mutableStateOf(false)

    // Errors
    var nameError by mutableStateOf<String?>(null)
    var vinError by mutableStateOf<String?>(null)

    fun onNameChange(newName: String) {
        legalName = newName
        nameError = if (newName.isBlank()) "Legal name is required" else null
    }

    fun submitRegistration() {
        if (!validate()) return

        uiState = RegistrationUiState.Loading
        viewModelScope.launch {
            val registration = OwnerRegistration(
                legalName = legalName,
                proofOfIdentityUri = proofOfIdentityUri,
                proofOfAddressUri = proofOfAddressUri,
                vehicleRegistrationUri = vehicleRegistrationUri,
                vehicleInsuranceUri = vehicleInsuranceUri,
                operatorsLicenseUri = operatorsLicenseUri,
                make = make,
                model = model,
                year = year,
                vin = vin,
                licensePlate = licensePlate,
                engineNumber = engineNumber,
                purposeOfUse = purposeOfUse,
                bankAccountName = bankAccountName,
                bankAccountNumber = bankAccountNumber,
                bankName = bankName,
                backgroundCheckConsent = backgroundCheckConsent
            )
            
            val result = repository.registerOwner(registration)
            
            uiState = if (result.isSuccess) {
                RegistrationUiState.Success
            } else {
                RegistrationUiState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    private fun validate(): Boolean {
        onNameChange(legalName)
        vinError = if (vin.isBlank()) "VIN is required" else null
        return nameError == null && vinError == null
    }

    fun resetState() {
        uiState = RegistrationUiState.Idle
    }
}
