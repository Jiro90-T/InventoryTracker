package com.jiro.inventorytracker.ui.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jiro.inventorytracker.data.Item
import com.jiro.inventorytracker.domain.ItemRepository
import com.jiro.inventorytracker.media.PhotoStorage
import com.jiro.inventorytracker.persona.Persona
import com.jiro.inventorytracker.persona.UserPreferences
import com.jiro.inventorytracker.reminders.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.concurrent.TimeUnit
import kotlin.math.max

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val repository: ItemRepository,
    private val reminderScheduler: ReminderScheduler,
    private val userPreferences: UserPreferences,
    private val photoStorage: PhotoStorage,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val itemId: Long = savedStateHandle.get<Long>("itemId") ?: 0L
    private val initialBarcode: String? = savedStateHandle.get<String>("prefilledBarcode")

    private val _state = MutableStateFlow(AddEditUiState())
    val state: StateFlow<AddEditUiState> = _state.asStateFlow()

    val persona: StateFlow<Persona> = userPreferences.persona
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Persona.HOME)

    val isEditMode: Boolean get() = itemId != 0L

    /**
     * Initial values of the form, captured on first load. Used to detect whether
     * the user has unsaved changes before they navigate away.
     */
    private var initialSnapshot: AddEditUiState = AddEditUiState()

    val isDirty: StateFlow<Boolean> = _state
        .map { it != initialSnapshot }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init {
        if (itemId != 0L) {
            viewModelScope.launch {
                val existing = repository.get(itemId)
                if (existing != null) {
                    val loaded = AddEditUiState.fromItem(existing)
                    _state.value = loaded
                    initialSnapshot = loaded
                }
            }
        } else if (initialBarcode != null) {
            _state.update { it.copy(barcode = initialBarcode) }
            initialSnapshot = _state.value
        } else {
            initialSnapshot = _state.value
        }
    }

    fun update(transform: (AddEditUiState) -> AddEditUiState) {
        _state.update(transform)
    }

    fun addPhoto(uri: String) {
        _state.update { it.copy(photoPaths = it.photoPaths + uri) }
    }

    fun removePhoto(uri: String) {
        // Drop from state, then delete the file. We only delete files we own
        // (paths under app-internal storage); arbitrary content URIs are left
        // alone by PhotoStorage.
        _state.update { it.copy(photoPaths = it.photoPaths - uri) }
        viewModelScope.launch { photoStorage.deleteIfOwned(uri) }
    }

    fun save(onDone: (Long) -> Unit) {
        val s = _state.value
        if (s.name.isBlank()) {
            _state.update { it.copy(error = "Name is required") }
            return
        }
        viewModelScope.launch {
            val item = Item(
                id = if (isEditMode) itemId else 0L,
                name = s.name.trim(),
                category = s.category.trim().ifBlank { null },
                barcode = s.barcode.trim().ifBlank { null },
                quantity = max(1, s.quantity),
                location = s.location.trim().ifBlank { null },
                purchaseDate = s.purchaseDate,
                purchasePrice = s.purchasePrice,
                purchaseCurrency = s.purchaseCurrency.ifBlank { null },
                currentValue = s.currentValue,
                warrantyExpiresAt = s.warrantyExpiresAt,
                expiryDate = s.expiryDate,
                photoPaths = s.photoPaths,
                notes = s.notes.trim().ifBlank { null },
                condition = s.condition?.name,
                grade = s.grade.trim().ifBlank { null },
                era = s.era.trim().ifBlank { null },
                assignedTo = s.assignedTo.trim().ifBlank { null },
                assetTag = s.assetTag.trim().ifBlank { null },
                manufacturer = s.manufacturer.trim().ifBlank { null },
                model = s.model.trim().ifBlank { null },
                serialNumber = s.serialNumber.trim().ifBlank { null }
            )
            val id = repository.upsert(item)
            scheduleRemindersFor(id, item)
            onDone(id)
        }
    }

    fun delete(onDone: () -> Unit) {
        if (!isEditMode) {
            onDone()
            return
        }
        viewModelScope.launch {
            val paths = repository.photoPathsFor(itemId)
            paths.forEach { photoStorage.deleteIfOwned(it) }
            repository.delete(itemId)
            onDone()
        }
    }

    fun hasPhotos(): Boolean = _state.value.photoPaths.isNotEmpty()

    private suspend fun scheduleRemindersFor(id: Long, item: Item) {
        val warrantyLeadMs = TimeUnit.DAYS.toMillis(
            userPreferences.warrantyLeadDays.first().toLong()
        )
        val expiryLeadMs = TimeUnit.DAYS.toMillis(
            userPreferences.expiryLeadDays.first().toLong()
        )
        item.warrantyExpiresAt?.let { ts ->
            reminderScheduler.schedule(
                itemId = id,
                triggerAtMillis = ts - warrantyLeadMs,
                title = "Warranty ending soon",
                message = "${item.name}'s warranty expires soon."
            )
        }
        item.expiryDate?.let { ts ->
            reminderScheduler.schedule(
                itemId = id,
                triggerAtMillis = ts - expiryLeadMs,
                title = "Item expiring soon",
                message = "${item.name} expires soon."
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
