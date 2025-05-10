package com.example.rinzelweather.logic.model

import java.util.Date


data class DailyResponse(val status: String, val result: Result) {
    data class Result(val daily: Daily)

    data class Daily(val temperature: List<Temperature>, val skycon: List<Skycon>,
        val coldRisk: List<LifeDescription>, val carWashing: List<LifeDescription>, val ultraviolet: List<LifeDescription>,
        val dressing: List<LifeDescription>)

    data class Temperature(val date: Date, val min: Float, val max: Float, val avg: Float)

    data class Skycon(val date: Date, val value: String)

    data class LifeDescription(val datetime: Date, val index: Int, val desc: String)
}
