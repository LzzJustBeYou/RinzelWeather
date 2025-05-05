package com.example.rinzelweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class RinzelWeatherApplication: Application() {
    companion object {
        const val TOKEN = "SwDyJjnr8wancq3N"

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}