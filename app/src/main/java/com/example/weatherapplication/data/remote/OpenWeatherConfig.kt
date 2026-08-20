package com.example.weatherapplication.data.remote

import com.example.weatherapplication.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Abstraction over BuildConfig so unit tests can supply a fake API key / base URLs
 * without depending on generated Android BuildConfig values.
 */
interface OpenWeatherConfig {
    val apiKey: String
    val iconBaseUrl: String
}

@Singleton
class BuildConfigOpenWeatherConfig @Inject constructor() : OpenWeatherConfig {
    override val apiKey: String get() = BuildConfig.OPENWEATHER_API_KEY
    override val iconBaseUrl: String get() = BuildConfig.OPENWEATHER_ICON_BASE_URL
}
