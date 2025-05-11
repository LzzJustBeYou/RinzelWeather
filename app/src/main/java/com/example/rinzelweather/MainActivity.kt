package com.example.rinzelweather

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.rinzelweather.logic.model.Place
import com.example.rinzelweather.logic.repository.PlaceRepository
import com.example.rinzelweather.ui.werther.WeatherActivity
import com.example.rinzelweather.utils.LocationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    
    // 位置权限请求
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            // 权限已授予，获取位置并跳转
            getLocationAndNavigate()
        } else {
            // 用户拒绝了权限
            if (shouldShowRequestPermissionRationale()) {
                LocationUtils.showPermissionRationaleDialog(this) {
                    requestLocationPermissions()
                }
            } else {
                // 用户选择了"不再询问"
                LocationUtils.showPermissionDeniedDialog(this)
                // 显示正常的主页面
                setupMainContent()
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 检查是否已有存储的位置和权限
        if (PlaceRepository.isPlaceSaved() && LocationUtils.hasLocationPermissions(this)) {
            // 已有位置和权限，直接跳转到天气页面
            startWeatherActivity()
            return
        }
        
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // 检查并请求位置权限
        if (LocationUtils.hasLocationPermissions(this)) {
            // 有权限但没有保存的位置，获取位置并跳转
            getLocationAndNavigate()
        } else {
            // 请求位置权限
            requestLocationPermissions()
        }
    }
    
    private fun requestLocationPermissions() {
        if (shouldShowRequestPermissionRationale()) {
            // 显示权限请求理由
            LocationUtils.showPermissionRationaleDialog(this) {
                LocationUtils.requestLocationPermissions(this, requestPermissionLauncher)
            }
        } else {
            // 直接请求权限
            LocationUtils.requestLocationPermissions(this, requestPermissionLauncher)
        }
    }
    
    private fun shouldShowRequestPermissionRationale(): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            this, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) || ActivityCompat.shouldShowRequestPermissionRationale(
            this, android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
    
    private fun getLocationAndNavigate() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, R.string.getting_location, Toast.LENGTH_SHORT).show()
                }
                
                // 获取当前位置
                val location = LocationUtils.getCurrentLocation(this@MainActivity)
                
                // 加载城市列表并查找最近的城市
                val cities = PlaceRepository.getPlaces()
                val nearestCity = LocationUtils.findNearestCity(location, cities)
                
                if (nearestCity != null) {
                    Log.d(TAG, "找到最近的城市: ${nearestCity.name}")
                    
                    // 保存找到的城市
                    PlaceRepository.savePlace(nearestCity)
                    
                    // 跳转到天气页面
                    withContext(Dispatchers.Main) {
                        startWeatherActivity()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        // 没有找到城市，显示主页面
                        setupMainContent()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "获取位置失败", e)
                withContext(Dispatchers.Main) {
                    // 获取位置失败，显示主页面
                    setupMainContent()
                }
            }
        }
    }
    
    private fun startWeatherActivity() {
        val intent = Intent(this, WeatherActivity::class.java)
        startActivity(intent)
        finish() // 结束当前Activity
    }
    
    private fun setupMainContent() {
        // 这里不需要额外操作，因为已经在onCreate中设置了contentView
        // 如果需要，可以在这里初始化其他UI组件
    }
}