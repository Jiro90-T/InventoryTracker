package com.jiro.inventorytracker.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jiro.inventorytracker.persona.Persona
import com.jiro.inventorytracker.persona.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    val isOnboarded: StateFlow<Boolean> = userPreferences.hasOnboarded
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun complete(persona: Persona) {
        viewModelScope.launch {
            userPreferences.setPersona(persona)
            userPreferences.setOnboarded(true)
        }
    }
}
