package com.example.weatherapplication.Repository

import com.example.weatherapplication.network.WeatherApiService

class WeatherRepository(private val apiService: WeatherApiService) {
    suspend fun fetchWeatherData(apiKey: String, location: String) =
        apiService.getWeather(apiKey, location)
}
