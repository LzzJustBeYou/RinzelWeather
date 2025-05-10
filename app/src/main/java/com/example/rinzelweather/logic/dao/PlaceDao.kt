package com.example.rinzelweather.logic.dao

import android.content.Context
import com.example.rinzelweather.RinzelWeatherApplication
import com.example.rinzelweather.logic.model.Place
import com.google.gson.Gson
import androidx.core.content.edit

object PlaceDao {
    fun savePlace(place: Place) {
        sharedPreference().edit {
            putString("place", Gson().toJson(place))
        }
    }

    fun getSavedPlace(): Place? {
        val placeJson = sharedPreference().getString("place", "")
        return Gson().fromJson(placeJson, Place::class.java)
    }

    fun isPlaceSaved() = sharedPreference().contains("place")

    private fun sharedPreference() =
        RinzelWeatherApplication.context.getSharedPreferences("rinzel_weather", Context.MODE_PRIVATE)
}