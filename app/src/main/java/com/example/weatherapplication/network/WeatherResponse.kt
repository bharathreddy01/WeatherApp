package com.example.weatherapplication.network

import com.example.weatherapplication.network.WeatherApiService


import com.google.gson.annotations.SerializedName



data class WeatherResponse(
    val temperature: String, // Replace with the actual JSON fields
    val description: String
)
data class Main(
    @SerializedName("temp") val temperature: Double,
    @SerializedName("humidity") val humidity: Int
)

data class Weather(
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)
