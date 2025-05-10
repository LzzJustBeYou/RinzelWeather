package com.example.rinzelweather.logic.repository

import com.example.rinzelweather.logic.model.Weather
import com.example.rinzelweather.logic.network.RinzelWeatherNetwork
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

object WeatherRepository {
    suspend fun refreshWeather(lng: String, lat: String): Result<Weather> {
        return try {
            coroutineScope {
                val deferredRealtime = async {
                    RinzelWeatherNetwork.getRealtimeWeather(lng, lat)
                }
                val deferredDaily = async {
                    RinzelWeatherNetwork.getDailyWeather(lng, lat)
                }
                val realtimeResponse = deferredRealtime.await()
                val dailyResponse = deferredDaily.await()
                if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                    val weather = Weather(realtimeResponse.result, dailyResponse.result.daily)
                    Result.success((weather))
                } else {
                    Result.failure((
                            RuntimeException(
                                "realtime response status is ${realtimeResponse.status}" +
                                "daily response status is ${dailyResponse.status}"
                            )
                            ))
                }
            }
        } catch (e: Exception) {
            Result.failure<Weather>(e)
        }
    }
}