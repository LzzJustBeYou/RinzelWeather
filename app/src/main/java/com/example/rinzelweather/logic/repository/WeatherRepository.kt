package com.example.rinzelweather.logic.repository

import com.example.rinzelweather.logic.model.Weather
import com.example.rinzelweather.logic.network.RinzelWeatherNetwork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

object WeatherRepository {
    fun getWeatherFlow(lng: String, lat: String): Flow<Result<Weather>> = flow {
        try {
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
                    emit(Result.success(weather))
                } else {
                    emit(Result.failure(
                        RuntimeException(
                            "realtime response status is ${realtimeResponse.status}" +
                            "daily response status is ${dailyResponse.status}"
                        )
                    ))
                }
            }
        } catch (e: Exception) {
            emit(Result.failure<Weather>(e))
        }
    }
}