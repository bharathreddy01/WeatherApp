package com.example.weatherapplication.data.repository

import com.example.weatherapplication.data.local.WeatherPreferences
import com.example.weatherapplication.data.model.WeatherInfo
import com.example.weatherapplication.data.remote.OpenWeatherConfig
import com.example.weatherapplication.data.remote.WeatherApiService
import com.example.weatherapplication.data.remote.WeatherMapper
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

sealed class WeatherResult {
    data class Success(val weather: WeatherInfo) : WeatherResult()
    data class Failure(val reason: WeatherFailure) : WeatherResult()
}

sealed class WeatherFailure {
    object MissingApiKey : WeatherFailure()
    object EmptyCity : WeatherFailure()
    object CityNotFound : WeatherFailure()
    object Network : WeatherFailure()
    object Unauthorized : WeatherFailure()
    data class Unknown(val detail: String? = null) : WeatherFailure()
}

/**
 * Single source of truth for weather fetches and last-city persistence.
 */
@Singleton
class WeatherRepository @Inject constructor(
    private val api: WeatherApiService,
    private val preferences: WeatherPreferences,
    private val config: OpenWeatherConfig
) {

    fun getLastCity(): String? = preferences.getLastCity()

    suspend fun fetchByCity(rawCity: String): WeatherResult {
        val city = rawCity.trim()
        if (city.isEmpty()) return WeatherResult.Failure(WeatherFailure.EmptyCity)

        val apiKey = config.apiKey
        if (apiKey.isBlank()) return WeatherResult.Failure(WeatherFailure.MissingApiKey)

        // Restrict lookups to the US per challenge ("enter a US city").
        val query = if (city.contains(",")) city else "$city,US"

        return execute {
            val dto = api.getWeatherByCity(cityQuery = query, apiKey = apiKey)
            val info = WeatherMapper.toWeatherInfo(dto, config.iconBaseUrl)
                ?: return@execute WeatherResult.Failure(WeatherFailure.Unknown("Incomplete weather payload"))
            preferences.saveLastCity(info.cityName)
            WeatherResult.Success(info)
        }
    }

    suspend fun fetchByCoordinates(latitude: Double, longitude: Double): WeatherResult {
        val apiKey = config.apiKey
        if (apiKey.isBlank()) return WeatherResult.Failure(WeatherFailure.MissingApiKey)

        return execute {
            val dto = api.getWeatherByCoordinates(
                latitude = latitude,
                longitude = longitude,
                apiKey = apiKey
            )
            val info = WeatherMapper.toWeatherInfo(dto, config.iconBaseUrl)
                ?: return@execute WeatherResult.Failure(WeatherFailure.Unknown("Incomplete weather payload"))
            preferences.saveLastCity(info.cityName)
            WeatherResult.Success(info)
        }
    }

    private suspend fun execute(block: suspend () -> WeatherResult): WeatherResult {
        return try {
            block()
        } catch (e: HttpException) {
            when (e.code()) {
                401, 403 -> WeatherResult.Failure(WeatherFailure.Unauthorized)
                404 -> WeatherResult.Failure(WeatherFailure.CityNotFound)
                else -> WeatherResult.Failure(
                    WeatherFailure.Unknown("HTTP ${e.code()}")
                )
            }
        } catch (_: IOException) {
            WeatherResult.Failure(WeatherFailure.Network)
        } catch (e: Exception) {
            WeatherResult.Failure(WeatherFailure.Unknown(e.message))
        }
    }
}
