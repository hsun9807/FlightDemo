package com.example.flightdemo.model

import com.example.flightdemo.response.FlightResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class FlightModel {

    private val client = OkHttpClient()
    private val gson = Gson()

    // 高雄國際機場 API
    // AirFlyLine 航線
    // AirFlyIO 1>起飛 2>抵達
    private val baseUrl = "https://www.kia.gov.tw/API/InstantSchedule.ashx"

    suspend fun fetchFlightData(airFlyLine: Int, airFlyIO: Int): FlightResponse? {
        return withContext(Dispatchers.IO) {
            try {
                // 根據 position 決定 AirFlyIO 參數
                val Line = if (airFlyLine == 0) "1" else "2"
                val IO = if (airFlyIO == 0) "1" else "2"
                val requestUrl = "$baseUrl?AirFlyLine=$Line&AirFlyIO=$IO"

                val request = Request.Builder()
                    .url(requestUrl)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        response.body?.string()?.let { json ->
                            gson.fromJson(json, FlightResponse::class.java)
                        }
                    } else {
                        null
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
