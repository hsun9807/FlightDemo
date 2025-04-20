package com.example.flightdemo.response

data class ExchangeResponse(
    val data: Map<String, Double>?
)

data class ExchangeItem(
    val currency: String,
    val rate: Double,
    val money: Double
)
