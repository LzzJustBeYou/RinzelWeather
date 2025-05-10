package com.example.rinzelweather.logic.model

import com.example.rinzelweather.R


class Sky(val info: String, val icon: Int, val bg: Int)

fun getSky(skycon: String): Sky {
    return when (skycon) {
        "CLEAR_DAY" -> Sky("晴", R.drawable.ic_clear_day, R.drawable.bg_clear_day)
        "CLEAR_NIGHT" -> Sky("晴", R.drawable.ic_clear_night, R.drawable.bg_clear_night)
        "PARTLY_CLOUDY_DAY" -> Sky("多云", R.drawable.ic_partly_cloud_day, R.drawable.bg_partly_cloudy_day)
        "PARTLY_CLOUDY_NIGHT" -> Sky("多云", R.drawable.ic_partly_cloud_night, R.drawable.bg_partly_cloudy_night)
        "CLOUDY" -> Sky("阴", R.drawable.ic_cloudy, R.drawable.bg_cloudy)
        "WIND" -> Sky("大风", R.drawable.ic_cloudy, R.drawable.bg_wind)
        "LIGHT_RAIN" -> Sky("小雨", R.drawable.ic_light_rain, R.drawable.bg_rain)
        "MODERATE_RAIN" -> Sky("中雨", R.drawable.ic_moderate_rain, R.drawable.bg_rain)
        "HEAVY_RAIN" -> Sky("大雨", R.drawable.ic_heavy_rain, R.drawable.bg_rain)
        "STORM_RAIN" -> Sky("暴雨", R.drawable.ic_storm_rain, R.drawable.bg_rain)
        "THUNDER_SHOWER" -> Sky("雷阵雨", R.drawable.ic_thunder_shower, R.drawable.bg_rain)
        "SLEET" -> Sky("雨夹雪", R.drawable.ic_sleet, R.drawable.bg_rain)
        "LIGHT_SNOW" -> Sky("小雪", R.drawable.ic_light_snow, R.drawable.bg_snow)
        "MODERATE_SNOW" -> Sky("中雪", R.drawable.ic_moderate_snow, R.drawable.bg_snow)
        "HEAVY_SNOW" -> Sky("大雪", R.drawable.ic_heavy_snow, R.drawable.bg_snow)
        "STORM_SNOW" -> Sky("暴雪", R.drawable.ic_heavy_snow, R.drawable.bg_snow)
        "HAIL" -> Sky("冰雹", R.drawable.ic_hail, R.drawable.bg_snow)
        "LIGHT_HAZE" -> Sky("轻度雾霾", R.drawable.ic_light_haze, R.drawable.bg_fog)
        "MODERATE_HAZE" -> Sky("中度雾霾", R.drawable.ic_moderate_haze, R.drawable.bg_fog)
        "HEAVY_HAZE" -> Sky("重度雾霾", R.drawable.ic_heavy_haze, R.drawable.bg_fog)
        "FOG" -> Sky("雾", R.drawable.ic_fog, R.drawable.bg_fog)
        "DUST" -> Sky("浮尘", R.drawable.ic_fog, R.drawable.bg_fog)
        // 只有雨/雪/雾霾，没有返回具体的等级
        "HAZE" -> Sky("雾", R.drawable.ic_moderate_haze, R.drawable.bg_fog)
        "RAIN" -> Sky("下雨", R.drawable.ic_moderate_rain, R.drawable.bg_rain)
        "SNOW" -> Sky("下雪", R.drawable.ic_moderate_snow, R.drawable.bg_snow)
        else -> Sky("未知", R.drawable.ic_clear_day, R.drawable.bg_clear_day)
    }
}

