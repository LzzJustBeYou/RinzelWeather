package com.example.rinzelweather.ui.werther

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.rinzelweather.R
import com.example.rinzelweather.logic.model.Weather
import com.example.rinzelweather.logic.model.getSky
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WeatherActivity: AppCompatActivity() {
    private val TAG = "WeatherActivity"

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    private lateinit var navBtn: ImageView
    private lateinit var drawableLayout: DrawerLayout
    private lateinit var swipeRefresh: SwipeRefreshLayout
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

        setupView()
        observeData()
        initListener()
    }

    private fun setupView() {
        // 设置初始可见性
        weatherLayout = findViewById(R.id.weather_layout)
        weatherLayout.visibility = View.VISIBLE // 临时设置为可见，测试布局是否正确显示

        swipeRefresh = findViewById(R.id.swipe_refresh)
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)

        // 初始化控件
        navBtn = findViewById(R.id.nav_button)
        drawableLayout = findViewById(R.id.drawer_layout)
        placeName = findViewById(R.id.place_name)
        currentTemp = findViewById(R.id.current_temp)
        currentSky = findViewById(R.id.current_sky)
        currentAQI = findViewById(R.id.current_aqi)
        forecastLayout = findViewById(R.id.forecast_layout)
        coldRiskText = findViewById(R.id.cold_risk_text)
        dressingText = findViewById(R.id.dressing_text)
        ultravioletText = findViewById(R.id.ultraviolet_text)
        carWashingText = findViewById(R.id.card_washing_text)
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.savedPlaceFlow.collectLatest { place ->
                if (place !== null) {
                    placeName.text = place.name
                }
            }
        }

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
                swipeRefresh.isRefreshing = isLoading
            }
        }
    }

    private fun initListener() {
        swipeRefresh.setOnRefreshListener {
            viewModel.reloadWeather()
        }

        navBtn.setOnClickListener {
            drawableLayout.openDrawer(GravityCompat.START)
        }

        drawableLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
            }

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        })
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