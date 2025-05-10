package com.example.rinzelweather.logic.model

data class Weather(val realtime: RealtimeResponse.Result, val daily: DailyResponse.Daily)
