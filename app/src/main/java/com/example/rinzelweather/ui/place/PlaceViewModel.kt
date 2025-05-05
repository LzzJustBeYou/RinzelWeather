package com.example.rinzelweather.ui.place

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rinzelweather.logic.model.Place
import com.example.rinzelweather.logic.repository.PlaceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class PlaceViewModel : ViewModel() {
    // 搜索查询的状态流
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // 加载状态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 获取所有区域的Flow
    private val placesFlow = flow {
        _isLoading.value = true
        try {
            emit(PlaceRepository.getPlaces())
        } finally {
            _isLoading.value = false
        }
    }

    // 根据搜索查询过滤区域
    @OptIn(ExperimentalCoroutinesApi::class)
    val filterPlaces: StateFlow<List<Place>> = searchQuery
        .flatMapLatest { query ->
            placesFlow.combine(flow { emit(query) }) { places, searchText ->
                if (searchText.isEmpty()) {
                    places
                } else {
                    places.filter { place ->
                        place.name.contains(searchText, ignoreCase = true)
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 更新搜索查询
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}