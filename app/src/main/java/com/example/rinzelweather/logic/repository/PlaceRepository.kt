package com.example.rinzelweather.logic.repository

import android.util.Log
import com.example.rinzelweather.logic.model.Place
import com.example.rinzelweather.utils.CsvLoader

object PlaceRepository {
    private var cachePlaces: List<Place>? = null

    fun getPlaces(): List<Place> {
        val res = cachePlaces ?: CsvLoader.loadRegionCsv().also {
            cachePlaces = it
        }
        Log.d("PlaceRepository", "places is: $cachePlaces")
        return  res
    }
}