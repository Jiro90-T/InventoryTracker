package com.jiro.inventorytracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jiro.inventorytracker.data.Item
import com.jiro.inventorytracker.domain.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ItemRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    @OptIn(ExperimentalCoroutinesApi::class)
    val items: StateFlow<List<Item>> = _query
        .flatMapLatest { q ->
            if (q.isBlank()) repository.observeAll() else repository.search(q.trim())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setQuery(value: String) {
        _query.value = value
    }
}
