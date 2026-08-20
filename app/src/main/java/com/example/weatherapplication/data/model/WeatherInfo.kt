package com.example.weatherapplication.data.model

/**
 * UI / domain weather model — decoupled from Retrofit DTOs so ViewModels and tests
 * do not depend on Gson annotations or JSON shape.
 */
data class WeatherInfo(
    val cityName: String,
    val countryCode: String?,
    val temperatureF: Double,
    val feelsLikeF: Double,
    val tempMinF: Double,
    val tempMaxF: Double,
    val humidityPercent: Int,
    val pressureHpa: Int,
    val windSpeedMph: Double,
    val description: String,
    val conditionMain: String,
    /** OpenWeather icon code, e.g. "01d". Used to build the CDN URL. */
    val iconCode: String,
    val iconUrl: String
)
