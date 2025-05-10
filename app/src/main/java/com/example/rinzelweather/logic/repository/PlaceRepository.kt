package com.example.rinzelweather.logic.repository

import android.util.Log
import com.example.rinzelweather.logic.dao.PlaceDao
import com.example.rinzelweather.logic.model.Place
import com.example.rinzelweather.utils.CsvLoader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

object PlaceRepository {
    private var cachePlaces: List<Place>? = null
    private val placeFlow = MutableStateFlow<Place?>(null)

    init {
        placeFlow.value = getSavedPlace()
    }

    fun getPlaces(): List<Place> {
        val res = cachePlaces ?: CsvLoader.loadRegionCsv().also {
            cachePlaces = it
        }
        Log.d("PlaceRepository", "places is: $cachePlaces")
        return  res
    }

    fun savePlace(place: Place) {
        PlaceDao.savePlace(place)
        placeFlow.value = place
    }

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

    fun getSavedPlaceFlow() = placeFlow
}