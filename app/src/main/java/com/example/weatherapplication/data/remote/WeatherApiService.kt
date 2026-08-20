package com.example.weatherapplication.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * OpenWeatherMap Current Weather endpoints.
 *
 * City-name lookup is deprecated by OWM but still documented/available and is
 * explicitly allowed by the coding challenge. Location flow uses lat/lon instead.
 */
interface WeatherApiService {

    @GET("data/2.5/weather")
    suspend fun getWeatherByCity(
        @Query("q") cityQuery: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "imperial"
    ): WeatherResponseDto

    @GET("data/2.5/weather")
    suspend fun getWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "imperial"
    ): WeatherResponseDto
}
