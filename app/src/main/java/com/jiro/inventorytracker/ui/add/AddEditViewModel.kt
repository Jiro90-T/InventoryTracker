package com.jiro.inventorytracker.ui.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jiro.inventorytracker.data.Item
import com.jiro.inventorytracker.domain.ItemRepository
import com.jiro.inventorytracker.reminders.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val repository: ItemRepository,
    private val reminderScheduler: ReminderScheduler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val itemId: Long = savedStateHandle.get<Long>("itemId") ?: 0L
    private val initialBarcode: String? = savedStateHandle.get<String>("prefilledBarcode")

    private val _state = MutableStateFlow(AddEditUiState())
    val state: StateFlow<AddEditUiState> = _state.asStateFlow()

    val isEditMode: Boolean get() = itemId != 0L

    init {
        if (itemId != 0L) {
            viewModelScope.launch {
                val existing = repository.get(itemId)
                if (existing != null) {
                    _state.value = AddEditUiState.fromItem(existing)
                }
            }
        } else if (initialBarcode != null) {
            _state.update { it.copy(barcode = initialBarcode) }
        }
    }

    fun update(transform: (AddEditUiState) -> AddEditUiState) {
        _state.update(transform)
    }

    fun addPhoto(uri: String) {
        _state.update { it.copy(photoPaths = it.photoPaths + uri) }
    }

    fun removePhoto(uri: String) {
        _state.update { it.copy(photoPaths = it.photoPaths - uri) }
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
                warrantyExpiresAt = s.warrantyExpiresAt,
                expiryDate = s.expiryDate,
                photoPaths = s.photoPaths,
                notes = s.notes.trim().ifBlank { null }
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
            repository.delete(itemId)
            onDone()
        }
    }

    private fun scheduleRemindersFor(id: Long, item: Item) {
        item.warrantyExpiresAt?.let { ts ->
            reminderScheduler.schedule(
                itemId = id,
                triggerAtMillis = ts - THIRTY_DAYS_MS,
                title = "Warranty ending soon",
                message = "${item.name}'s warranty expires in ~30 days."
            )
        }
        item.expiryDate?.let { ts ->
            reminderScheduler.schedule(
                itemId = id,
                triggerAtMillis = ts - SEVEN_DAYS_MS,
                title = "Item expiring soon",
                message = "${item.name} expires in ~7 days."
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    companion object {
        private const val THIRTY_DAYS_MS = 30L * 24 * 60 * 60 * 1000
        private const val SEVEN_DAYS_MS = 7L * 24 * 60 * 60 * 1000
    }
}
