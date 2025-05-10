package com.example.rinzelweather.logic.network

import com.example.rinzelweather.RinzelWeatherApplication
import com.example.rinzelweather.logic.model.DailyResponse
import com.example.rinzelweather.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {
//    curl "https://api.caiyunapp.com/v2.3/TAkhjf8d1nlSlspN/101.6656,39.2072/realtime"
    @GET("v2.3/${RinzelWeatherApplication.TOKEN}/{lng},{lat}/realtime")
    fun getRealtimeWeather(@Path("lng") lng: String, @Path("lat") lat: String): Call<RealtimeResponse>

//    curl "https://api.caiyunapp.com/v2.3/TAkhjf8d1nlSlspN/101.6656,39.2072/daily?dailysteps=1"
    @GET("v2.3/${RinzelWeatherApplication.TOKEN}/{lng},{lat}/daily?dailysteps=7")
    fun getDailyWeather(@Path("lng") lng: String, @Path("lat") lat: String): Call<DailyResponse>
}