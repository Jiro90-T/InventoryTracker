package com.jiro.inventorytracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jiro.inventorytracker.domain.ItemRepository
import com.jiro.inventorytracker.export.CsvExporter
import com.jiro.inventorytracker.persona.Persona
import com.jiro.inventorytracker.persona.ThemeMode
import com.jiro.inventorytracker.persona.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val repository: ItemRepository,
    private val csvExporter: CsvExporter
) : ViewModel() {

    val persona: StateFlow<Persona> = userPreferences.persona
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Persona.HOME)
    val currency: StateFlow<String> = userPreferences.currency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserPreferences.DEFAULT_CURRENCY)
    val warrantyLeadDays: StateFlow<Int> = userPreferences.warrantyLeadDays
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserPreferences.DEFAULT_WARRANTY_LEAD_DAYS)
    val expiryLeadDays: StateFlow<Int> = userPreferences.expiryLeadDays
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserPreferences.DEFAULT_EXPIRY_LEAD_DAYS)
    val themeMode: StateFlow<ThemeMode> = userPreferences.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeMode.SYSTEM)

    fun setPersona(p: Persona) = viewModelScope.launch { userPreferences.setPersona(p) }
    fun setCurrency(c: String) = viewModelScope.launch { userPreferences.setCurrency(c) }
    fun setWarrantyLeadDays(d: Int) = viewModelScope.launch { userPreferences.setWarrantyLeadDays(d) }
    fun setExpiryLeadDays(d: Int) = viewModelScope.launch { userPreferences.setExpiryLeadDays(d) }
    fun setThemeMode(m: ThemeMode) = viewModelScope.launch { userPreferences.setThemeMode(m) }

    fun buildCsvAsync(onReady: (String) -> Unit) {
        viewModelScope.launch {
            onReady(csvExporter.toCsv())
        }
    }
}
