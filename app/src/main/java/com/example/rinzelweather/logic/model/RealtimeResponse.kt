package com.example.rinzelweather.logic.model

data class RealtimeResponse(val status: String, val result: Result) {
    data class Result(val temperature: Float, val skycon: String, val aqi: Float)
}
