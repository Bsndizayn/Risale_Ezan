package com.example.risaleezanvakticompose.presentation.screen.tesbihat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.risaleezanvakticompose.data.repository.TesbihatRepository
import com.example.risaleezanvakticompose.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TesbihatViewModel @Inject constructor(
    private val repository: TesbihatRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<TesbihatCategory>>(emptyList())
    val categories: StateFlow<List<TesbihatCategory>> = _categories.asStateFlow()

    private val _selectedContent = MutableStateFlow<TesbihatContent?>(null)
    val selectedContent: StateFlow<TesbihatContent?> = _selectedContent.asStateFlow()

    private val _counters = MutableStateFlow<Map<String, Int>>(emptyMap())
    val counters: StateFlow<Map<String, Int>> = _counters.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _categories.value = repository.getAllCategories()
        }
    }

    fun selectCategory(category: TesbihatCategory) {
        viewModelScope.launch {
            val content = repository.getTesbihatContent(category)
            _selectedContent.value = content

            // Sayaçları sıfırla
            val initialCounters = content.items.associate { it.id to 0 }
            _counters.value = initialCounters
        }
    }

    fun incrementCounter(itemId: String) {
        val current = _counters.value[itemId] ?: 0
        val item = _selectedContent.value?.items?.find { it.id == itemId }

        if (item != null && current < item.count) {
            _counters.value = _counters.value.toMutableMap().apply {
                put(itemId, current + 1)
            }
        }
    }

    fun resetCounter(itemId: String) {
        _counters.value = _counters.value.toMutableMap().apply {
            put(itemId, 0)
        }
    }

    fun resetAllCounters() {
        val content = _selectedContent.value ?: return
        val initialCounters = content.items.associate { it.id to 0 }
        _counters.value = initialCounters
    }

    fun isItemCompleted(itemId: String): Boolean {
        val current = _counters.value[itemId] ?: 0
        val item = _selectedContent.value?.items?.find { it.id == itemId }
        return item != null && current >= item.count
    }

    fun getTotalProgress(): Float {
        val content = _selectedContent.value ?: return 0f
        if (content.items.isEmpty()) return 0f

        val totalRequired = content.items.sumOf { it.count }
        val totalCompleted = content.items.sumOf { item ->
            val current = _counters.value[item.id] ?: 0
            minOf(current, item.count)
        }

        return totalCompleted.toFloat() / totalRequired.toFloat()
    }
}