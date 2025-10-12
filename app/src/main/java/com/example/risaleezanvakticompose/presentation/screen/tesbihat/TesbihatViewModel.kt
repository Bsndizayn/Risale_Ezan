package com.example.risaleezanvakticompose.presentation.screen.tesbihat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.risaleezanvakticompose.data.repository.TesbihatRepository
import com.example.risaleezanvakticompose.domain.model.TesbihatCategory
import com.example.risaleezanvakticompose.domain.model.TesbihatSection
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

    private val _selectedCategory = MutableStateFlow<TesbihatCategory?>(null)
    val selectedCategory: StateFlow<TesbihatCategory?> = _selectedCategory.asStateFlow()

    private val _sections = MutableStateFlow<List<TesbihatSection>>(emptyList())
    val sections: StateFlow<List<TesbihatSection>> = _sections.asStateFlow()

    private val _htmlContent = MutableStateFlow<String>("")
    val htmlContent: StateFlow<String> = _htmlContent.asStateFlow()

    private val _scrollToId = MutableStateFlow<String?>(null)
    val scrollToId: StateFlow<String?> = _scrollToId.asStateFlow()

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
            _selectedCategory.value = category
            _sections.value = repository.getSections(category)
            _htmlContent.value = repository.getHtmlContent(category)
            _scrollToId.value = null // Reset scroll
        }
    }

    fun selectSection(section: TesbihatSection) {
        viewModelScope.launch {
            _scrollToId.value = section.scrollId
        }
    }

    fun clearScrollId() {
        _scrollToId.value = null
    }
}