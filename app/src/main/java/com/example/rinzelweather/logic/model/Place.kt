package com.example.rinzelweather.logic.model
import com.google.gson.annotations.SerializedName


// 地区基础信息类
data class Place(
    @SerializedName("code")          // 地区编码（如110000）
    val code: String,
    @SerializedName("name")          // 地区名称（如北京市）
    val name: String,
    @SerializedName("location")      // 经纬度对象
    val location: GeoLocation
)

// 经纬度坐标类
data class GeoLocation(
    @SerializedName("lng")          // 经度（对应CSV第三列）
    val longitude: Double,
    @SerializedName("lat")          // 纬度（对应CSV第四列）
    val latitude: Double
)