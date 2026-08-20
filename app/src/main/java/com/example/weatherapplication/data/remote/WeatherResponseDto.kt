package com.example.weatherapplication.data.remote

import com.google.gson.annotations.SerializedName

/**
 * OpenWeatherMap Current Weather API response (data/2.5/weather).
 * Only fields we surface in the UI are mapped; unknown JSON is ignored by Gson.
 */
data class WeatherResponseDto(
    @SerializedName("name") val cityName: String?,
    @SerializedName("weather") val weather: List<WeatherConditionDto>?,
    @SerializedName("main") val main: MainDto?,
    @SerializedName("wind") val wind: WindDto?,
    @SerializedName("sys") val sys: SysDto?,
    @SerializedName("visibility") val visibility: Int?,
    @SerializedName("cod") val cod: Int?,
    @SerializedName("message") val message: String?
)

data class WeatherConditionDto(
    @SerializedName("main") val main: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("icon") val icon: String?
)

data class MainDto(
    @SerializedName("temp") val temp: Double?,
    @SerializedName("feels_like") val feelsLike: Double?,
    @SerializedName("temp_min") val tempMin: Double?,
    @SerializedName("temp_max") val tempMax: Double?,
    @SerializedName("humidity") val humidity: Int?,
    @SerializedName("pressure") val pressure: Int?
)

data class WindDto(
    @SerializedName("speed") val speed: Double?,
    @SerializedName("deg") val deg: Int?
)

data class SysDto(
    @SerializedName("country") val country: String?
)
