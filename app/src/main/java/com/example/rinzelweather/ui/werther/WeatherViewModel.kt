package com.example.rinzelweather.ui.werther

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rinzelweather.logic.model.Place
import com.example.rinzelweather.logic.model.Weather
import com.example.rinzelweather.logic.repository.WeatherRepository
import com.example.rinzelweather.logic.repository.PlaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WeatherViewModel: ViewModel() {
    private val TAG = "WeatherViewModel"
    
    private val _refreshTrigger = MutableStateFlow(0L)
    private val _loadingFlow = MutableStateFlow(false)
    val loadingFlow: StateFlow<Boolean> = _loadingFlow

    val savedPlaceFlow = PlaceRepository.getSavedPlaceFlow()
        .catch { e ->
            Log.e(TAG, "已保存的地址失败：", e)
            emit(null)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    // 天气数据Flow
    val weatherFlow: StateFlow<Result<Weather>?> = combine(savedPlaceFlow, _refreshTrigger) { place, _ ->
        place
    }
        .flatMapLatest { place ->
            if (place != null) {
                val lng = place.location.longitude.toString()
                val lat = place.location.latitude.toString()
                if (lng.isNotEmpty() && lat.isNotEmpty()) {
                    _loadingFlow.value = true
                    WeatherRepository.getWeatherFlow(lng, lat)
                        .onCompletion { _loadingFlow.value = false }
                } else {
                    kotlinx.coroutines.flow.flow { 
                        emit(Result.failure<Weather>(IllegalStateException("位置信息为空")))
                    }.onCompletion { _loadingFlow.value = false }
                }
            } else {
                kotlinx.coroutines.flow.flow { 
                    emit(Result.failure<Weather>(IllegalStateException("未选择位置")))
                }.onCompletion { _loadingFlow.value = false }
            }
        }
        .catch { e ->
            Log.e(TAG, "获取天气数据出错", e)
            _loadingFlow.value = false
            emit(Result.failure(e))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    
    // 手动刷新天气
    fun reloadWeather() {
        _refreshTrigger.value = System.currentTimeMillis()
    }
}