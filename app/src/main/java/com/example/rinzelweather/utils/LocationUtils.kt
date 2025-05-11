package com.example.rinzelweather.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.example.rinzelweather.R
import com.example.rinzelweather.logic.model.Place
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object LocationUtils {
    private const val TAG = "LocationUtils"
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    /**
     * 检查位置权限是否已授予
     */
    fun hasLocationPermissions(context: Context): Boolean {
        return PERMISSIONS.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * 请求位置权限
     */
    fun requestLocationPermissions(activity: Activity, requestPermissionLauncher: ActivityResultLauncher<Array<String>>) {
        requestPermissionLauncher.launch(PERMISSIONS)
    }

    /**
     * 显示请求权限的理由
     */
    fun showPermissionRationaleDialog(activity: FragmentActivity, onRequestPermission: () -> Unit) {
        AlertDialog.Builder(activity)
            .setTitle(R.string.location_permission_title)
            .setMessage(R.string.location_permission_rationale)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                onRequestPermission()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
            .show()
    }

    /**
     * 显示权限被拒绝后的提示对话框
     */
    fun showPermissionDeniedDialog(activity: FragmentActivity) {
        AlertDialog.Builder(activity)
            .setTitle(R.string.location_permission_title)
            .setMessage(R.string.location_permission_denied)
            .setPositiveButton(R.string.open_settings) { _, _ ->
                // 打开应用设置页面
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", activity.packageName, null)
                }
                activity.startActivity(intent)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
            .show()
    }

    /**
     * 获取当前位置（协程挂起函数）
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Location = suspendCancellableCoroutine { continuation ->
        val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        val cancellationTokenSource = CancellationTokenSource()
        
        if (hasLocationPermissions(context)) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        Log.d(TAG, "获取到当前位置: ${location.latitude}, ${location.longitude}")
                        continuation.resume(location)
                    } else {
                        continuation.resumeWithException(Exception("无法获取位置信息"))
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "获取位置失败", e)
                    continuation.resumeWithException(e)
                }
            
            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        } else {
            continuation.resumeWithException(SecurityException("没有位置权限"))
        }
    }

    /**
     * 查找最近的城市
     */
    fun findNearestCity(location: Location, cities: List<Place>): Place? {
        if (cities.isEmpty()) return null
        
        return cities.minByOrNull { city ->
            val distance = calculateDistance(
                location.latitude, location.longitude,
                city.location.latitude, city.location.longitude
            )
            distance
        }
    }

    /**
     * 计算两点之间的距离
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return 6371 * c // 地球半径约为6371公里
    }
} 