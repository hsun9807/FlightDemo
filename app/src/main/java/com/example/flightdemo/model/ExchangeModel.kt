package com.example.flightdemo.model

import com.example.flightdemo.response.ExchangeResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class ExchangeModel {

    private val client = OkHttpClient()
    private val gson = Gson()

    private val baseUrl = "https://api.freecurrencyapi.com/v1/latest?" +
            "apikey=fca_live_vXo4je1wNMjSus44GUedA660Ah3E4qljIOUzVtHs"


    suspend fun fetchExchangeRates(baseCurrency: String): ExchangeResponse? {
        val url = baseUrl +
                "&base_currency=$baseCurrency" +
                "&currencies=JPY,USD,CNY,EUR,AUD,KRW"
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(url)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        response.body?.string()?.let { json ->
                            gson.fromJson(json, ExchangeResponse::class.java)
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

