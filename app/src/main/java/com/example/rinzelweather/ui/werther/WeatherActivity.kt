package com.example.rinzelweather.ui.werther

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.rinzelweather.R
import com.example.rinzelweather.logic.model.Weather
import com.example.rinzelweather.logic.model.getSky
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WeatherActivity: AppCompatActivity() {
    private val TAG = "WeatherActivity"

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    private lateinit var weatherLayout: ScrollView
    private lateinit var placeName: TextView
    private lateinit var currentTemp: TextView
    private lateinit var currentSky: TextView
    private lateinit var currentAQI: TextView
    private lateinit var forecastLayout: LinearLayout
    private lateinit var coldRiskText: TextView
    private lateinit var dressingText: TextView
    private lateinit var ultravioletText: TextView
    private lateinit var carWashingText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        
        // 设置初始可见性
        weatherLayout = findViewById(R.id.weather_layout)
        weatherLayout.visibility = View.VISIBLE // 临时设置为可见，测试布局是否正确显示
        
        // 初始化控件
        placeName = findViewById(R.id.place_name)
        currentTemp = findViewById(R.id.current_temp)
        currentSky = findViewById(R.id.current_sky)
        currentAQI = findViewById(R.id.current_aqi)
        forecastLayout = findViewById(R.id.forecast_layout)
        coldRiskText = findViewById(R.id.cold_risk_text)
        dressingText = findViewById(R.id.dressing_text)
        ultravioletText = findViewById(R.id.ultraviolet_text)
        carWashingText = findViewById(R.id.card_washing_text)
        
        // 从Intent中获取数据
        if (viewModel.currentLng.isEmpty()) {
            viewModel.currentLng = intent.getStringExtra("location_lng") ?: ""
            Log.d(TAG, "获取经度: ${viewModel.currentLng}")
        }
        if (viewModel.currentLat.isEmpty()) {
            viewModel.currentLat = intent.getStringExtra("location_lat") ?: ""
            Log.d(TAG, "获取纬度: ${viewModel.currentLat}")
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }

        // 在WeatherActivity的onCreate方法中添加这行调试代码
        Log.d(TAG, "Intent数据: lng=${intent.getStringExtra("location_lng")}, lat=${intent.getStringExtra("location_lat")}, name=${intent.getStringExtra("place_name")}")

        // 观察天气数据
        lifecycleScope.launch {
            viewModel.weatherFlow.collectLatest { result ->
                val weather = result?.getOrNull()
                Log.d(TAG, "result is: $result, weather is: $weather")
                if (weather != null) {
                    showWeatherInfo(weather)
                    weatherLayout.visibility = View.VISIBLE
                    Log.d(TAG, "天气数据加载成功，设置布局为可见")
                } else {
                    Log.d(TAG, "天气数据为空")
                }
            }
        }

        // 观察加载状态
        lifecycleScope.launch {
            viewModel.loadingFlow.collectLatest { isLoading ->
                // 可以在这里添加加载动画
            }
        }

        // 在加载数据前添加日志
        Log.d(TAG, "开始加载天气数据: lng=${viewModel.currentLng}, lat=${viewModel.currentLat}")

        // 加载天气数据
        viewModel.refreshWeather(viewModel.currentLng, viewModel.currentLat)
    }

    private fun showWeatherInfo(weather: Weather) {
        Log.d(TAG, "weather info is: $weather")
        val realtime = weather.realtime
        val daily = weather.daily

        // 填充 now.xml 布局中的数据
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        currentTemp.text = currentTempText
        
        // 显示天气状况
        currentSky.text = getSky(realtime.skycon).info

        // 显示空气质量
        val aqiText = "空气指数 ${realtime.aqi.toInt()}"
        currentAQI.text = aqiText
        
        // 设置背景图片（根据天气状况）
        val weatherLayout = findViewById<ScrollView>(R.id.weather_layout)
        weatherLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        
        // 填充forecast.xml布局
        forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = layoutInflater.inflate(R.layout.forecast_item, forecastLayout, false)
            val dateInfo = view.findViewById<TextView>(R.id.date_info)
            val skyIcon = view.findViewById<ImageView>(R.id.sky_icon)
            val skyInfo = view.findViewById<TextView>(R.id.sky_info)
            val temperatureInfo = view.findViewById<TextView>(R.id.temperature_info)
            
            val simpleDateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            
            forecastLayout.addView(view)
        }
        
        // 填充life_index.xml布局
        val lifeIndex = daily.coldRisk[0]
        coldRiskText.text = lifeIndex.desc

        val dressing = daily.dressing[0]
        dressingText.text = dressing.desc
        
        val ultraviolet = daily.ultraviolet[0]
        ultravioletText.text = ultraviolet.desc
        
        val carWashing = daily.carWashing[0]
        carWashingText.text = carWashing.desc
    }


}