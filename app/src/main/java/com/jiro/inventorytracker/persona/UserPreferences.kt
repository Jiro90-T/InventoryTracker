package com.jiro.inventorytracker.persona

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userPrefs by preferencesDataStore(name = "user_prefs")

/**
 * Persisted user preferences backed by DataStore. Exposes Flows so screens
 * can react to changes.
 */
@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val ds = context.userPrefs

    val persona: Flow<Persona> = ds.data.map { prefs ->
        Persona.fromName(prefs[KEY_PERSONA]) ?: Persona.HOME
    }

    val currency: Flow<String> = ds.data.map { it[KEY_CURRENCY] ?: DEFAULT_CURRENCY }

    val warrantyLeadDays: Flow<Int> = ds.data.map { it[KEY_WARRANTY_LEAD_DAYS] ?: DEFAULT_WARRANTY_LEAD_DAYS }
    val expiryLeadDays: Flow<Int> = ds.data.map { it[KEY_EXPIRY_LEAD_DAYS] ?: DEFAULT_EXPIRY_LEAD_DAYS }

    val themeMode: Flow<ThemeMode> = ds.data.map { prefs ->
        ThemeMode.fromName(prefs[KEY_THEME_MODE]) ?: ThemeMode.SYSTEM
    }

    val hasOnboarded: Flow<Boolean> = ds.data.map { it[KEY_HAS_ONBOARDED] == 1L }

    suspend fun setPersona(p: Persona) = ds.edit { it[KEY_PERSONA] = p.name }
    suspend fun setCurrency(c: String) = ds.edit { it[KEY_CURRENCY] = c }
    suspend fun setWarrantyLeadDays(days: Int) = ds.edit { it[KEY_WARRANTY_LEAD_DAYS] = days.coerceAtLeast(0) }
    suspend fun setExpiryLeadDays(days: Int) = ds.edit { it[KEY_EXPIRY_LEAD_DAYS] = days.coerceAtLeast(0) }
    suspend fun setThemeMode(mode: ThemeMode) = ds.edit { it[KEY_THEME_MODE] = mode.name }
    suspend fun setOnboarded(value: Boolean) = ds.edit { it[KEY_HAS_ONBOARDED] = if (value) 1L else 0L }

    companion object {
        const val DEFAULT_CURRENCY = "USD"
        const val DEFAULT_WARRANTY_LEAD_DAYS = 30
        const val DEFAULT_EXPIRY_LEAD_DAYS = 7

        private val KEY_PERSONA: Preferences.Key<String> = stringPreferencesKey("persona")
        private val KEY_CURRENCY: Preferences.Key<String> = stringPreferencesKey("currency")
        private val KEY_WARRANTY_LEAD_DAYS: Preferences.Key<Int> = intPreferencesKey("warranty_lead_days")
        private val KEY_EXPIRY_LEAD_DAYS: Preferences.Key<Int> = intPreferencesKey("expiry_lead_days")
        private val KEY_THEME_MODE: Preferences.Key<String> = stringPreferencesKey("theme_mode")
        private val KEY_HAS_ONBOARDED: Preferences.Key<Long> = longPreferencesKey("has_onboarded")
    }
}

enum class ThemeMode(val displayName: String) {
    SYSTEM("Follow system"),
    LIGHT("Light"),
    DARK("Dark");

    companion object {
        fun fromName(name: String?): ThemeMode? = entries.firstOrNull { it.name == name }
    }
}
