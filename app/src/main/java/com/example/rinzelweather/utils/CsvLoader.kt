package com.example.rinzelweather.utils

import android.util.Log
import com.example.rinzelweather.logic.model.GeoLocation
import com.example.rinzelweather.logic.model.Place
import com.opencsv.CSVReader
import java.io.InputStreamReader

object CsvLoader {
    private val TAG = "CsvLoader"

    fun loadCsv(path: String): List<Array<String>> {
        return try {
            javaClass.classLoader?.getResourceAsStream(path)?.use { inputStream ->
                CSVReader(InputStreamReader(inputStream, "UTF-8")).use { reader ->
                    reader.readAll()
                }
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "加载CSV文件失败: $path", e)
            emptyList()
        }
    }

    fun loadRegionCsv(): List<Place> {
        return try {
            val places = loadCsv("adcode.csv")
                .mapNotNull { row ->
                    try {
                        if (row.size < 4) {
                            Log.d(TAG, "CSV行数据不完整: ${row.joinToString()}")
                            null
                        } else {
                            Place(
                                code = row[0].trim(),
                                name = row[1].trim(),
                                location = GeoLocation(
                                    longitude = validateCoordinate(row[2], 73.66..135.05),
                                    latitude = validateCoordinate(row[3], 3.86..53.55)
                                )
                            )
                        }
                    } catch (e: Exception) {
                        Log.d(TAG, "无效数据行： ${row.joinToString()}, 错误${e.message}")
                        null
                    }
                }
            places
        } catch (e: Exception) {
            Log.e(TAG, "加载区域数据失败", e)
            emptyList()
        }
    }

    private fun validateCoordinate(value: String, range: ClosedRange<Double>): Double {
        val num = value.toDoubleOrNull()
            ?: throw IllegalArgumentException("坐标格式错误")
        if (num !in range) {
            throw IllegalArgumentException("坐标超出有效范围")
        }
        return num
    }
}