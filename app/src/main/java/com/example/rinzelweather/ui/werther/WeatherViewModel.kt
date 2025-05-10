package com.example.rinzelweather.ui.werther

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rinzelweather.logic.model.Weather
import com.example.rinzelweather.logic.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel: ViewModel() {
    private val _weatherFlow = MutableStateFlow<Result<Weather>?>(null)
    val weatherFlow: StateFlow<Result<Weather>?> = _weatherFlow.asStateFlow()
    
    private val _loadingFlow = MutableStateFlow(false)
    val loadingFlow: StateFlow<Boolean> = _loadingFlow.asStateFlow()
    
    var currentLng = ""
    var currentLat = ""
    var placeName = ""
    
    fun refreshWeather(lng: String, lat: String) {
        currentLng = lng
        currentLat = lat
        loadWeatherData()
    }
    
    fun reloadWeather() {
        if (currentLng.isNotEmpty() && currentLat.isNotEmpty()) {
            loadWeatherData()
        }
    }
    
    private fun loadWeatherData() {
        viewModelScope.launch {
            _loadingFlow.value = true
            try {
                val result = WeatherRepository.refreshWeather(currentLng, currentLat)
                _weatherFlow.value = result
            } catch (e: Exception) {
                _weatherFlow.value = Result.failure(e)
            } finally {
                _loadingFlow.value = false
            }
        }
    }
}