package com.jiro.inventorytracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jiro.inventorytracker.data.Item
import com.jiro.inventorytracker.data.ItemDao
import com.jiro.inventorytracker.domain.ItemRepository
import com.jiro.inventorytracker.persona.Persona
import com.jiro.inventorytracker.persona.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ItemRepository,
    itemDao: ItemDao,
    userPreferences: UserPreferences
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    val persona: StateFlow<Persona> = userPreferences.persona
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Persona.HOME)

    @OptIn(ExperimentalCoroutinesApi::class)
    val items: StateFlow<List<Item>> = combine(_query, _selectedCategory) { q, cat -> q to cat }
        .flatMapLatest { (q, cat) ->
            when {
                cat != null -> repository.filterByCategory(cat)
                q.isNotBlank() -> repository.search(q.trim())
                else -> repository.observeAll()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val categoryCounts: StateFlow<List<ItemDao.CategoryCount>> = itemDao.categoryCounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setQuery(value: String) {
        _query.value = value
    }

    fun setCategory(category: String?) {
        _selectedCategory.value = category
    }
}
