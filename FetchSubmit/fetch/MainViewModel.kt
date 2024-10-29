package com.arnav.fetch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

class MainViewModel : ViewModel() {

    private val _items = mutableStateOf<List<Item>>(emptyList())
    val items: State<List<Item>> = _items

    init {
        fetchItems()
    }

    private val _errorMessage = mutableStateOf<String?>(null)

    private fun fetchItems() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getItems()
                processData(response)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load data: ${e.message}"
            }
        }
    }

    private fun processData(items: List<Item>) {
        val filteredItems = items.filter { !it.name.isNullOrBlank() }

        val groupedItems = filteredItems.groupBy { it.listId }
            .toSortedMap()

        val sortedItems = groupedItems.flatMap { entry ->
            entry.value.sortedBy { it.name }
        }

        _items.value = sortedItems
    }
}