package com.jiro.inventorytracker.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jiro.inventorytracker.data.Item
import com.jiro.inventorytracker.domain.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    repository: ItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val itemId: Long = savedStateHandle.get<Long>("itemId") ?: 0L

    val item: StateFlow<Item?> = repository.observeById(itemId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
