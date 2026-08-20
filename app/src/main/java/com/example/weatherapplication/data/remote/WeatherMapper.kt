package com.example.weatherapplication.data.remote

import com.example.weatherapplication.data.model.WeatherInfo

/**
 * Maps OpenWeather DTOs into [WeatherInfo].
 * Returns null when required fields are missing so callers can treat that as a soft failure.
 */
object WeatherMapper {

    fun toWeatherInfo(dto: WeatherResponseDto, iconBaseUrl: String): WeatherInfo? {
        val main = dto.main ?: return null
        val temp = main.temp ?: return null
        val condition = dto.weather?.firstOrNull()
        val iconCode = condition?.icon?.takeIf { it.isNotBlank() } ?: return null
        val city = dto.cityName?.takeIf { it.isNotBlank() } ?: return null

        return WeatherInfo(
            cityName = city,
            countryCode = dto.sys?.country,
            temperatureF = temp,
            feelsLikeF = main.feelsLike ?: temp,
            tempMinF = main.tempMin ?: temp,
            tempMaxF = main.tempMax ?: temp,
            humidityPercent = main.humidity ?: 0,
            pressureHpa = main.pressure ?: 0,
            windSpeedMph = dto.wind?.speed ?: 0.0,
            description = condition.description?.replaceFirstChar { it.uppercase() }
                ?: condition.main
                ?: "Unknown",
            conditionMain = condition.main ?: "Unknown",
            iconCode = iconCode,
            iconUrl = buildIconUrl(iconBaseUrl, iconCode)
        )
    }

    fun buildIconUrl(iconBaseUrl: String, iconCode: String): String {
        // @2x PNG from OWM's weather-conditions CDN; Coil caches this on disk/memory.
        val normalized = if (iconBaseUrl.endsWith("/")) iconBaseUrl else "$iconBaseUrl/"
        return "${normalized}${iconCode}@2x.png"
    }
}
