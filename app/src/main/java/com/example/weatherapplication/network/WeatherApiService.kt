package com.example.weatherapplication.network

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("v1/current.json") // Replace with the actual endpoint of your API
    suspend fun getWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String
    ): WeatherResponse
}


