package com.example.weatherapplication.data.repository

import com.example.weatherapplication.data.local.WeatherPreferences
import com.example.weatherapplication.data.remote.MainDto
import com.example.weatherapplication.data.remote.OpenWeatherConfig
import com.example.weatherapplication.data.remote.SysDto
import com.example.weatherapplication.data.remote.WeatherApiService
import com.example.weatherapplication.data.remote.WeatherConditionDto
import com.example.weatherapplication.data.remote.WeatherResponseDto
import com.example.weatherapplication.data.remote.WindDto
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class WeatherRepositoryTest {

    private lateinit var api: WeatherApiService
    private lateinit var preferences: WeatherPreferences
    private lateinit var config: OpenWeatherConfig
    private lateinit var repository: WeatherRepository

    @Before
    fun setUp() {
        api = mock()
        preferences = mock()
        config = object : OpenWeatherConfig {
            override val apiKey: String = "test-key"
            override val iconBaseUrl: String = "https://openweathermap.org/img/wn/"
        }
        repository = WeatherRepository(api, preferences, config)
    }

    @Test
    fun `fetchByCity returns EmptyCity for blank input`() = runTest {
        val result = repository.fetchByCity("   ")
        assertThat(result).isEqualTo(WeatherResult.Failure(WeatherFailure.EmptyCity))
        verify(api, never()).getWeatherByCity(any(), any(), any())
    }

    @Test
    fun `fetchByCity appends US country code and saves last city`() = runTest {
        whenever(api.getWeatherByCity(eq("Austin,US"), eq("test-key"), any()))
            .thenReturn(sampleDto("Austin"))

        val result = repository.fetchByCity("Austin")

        assertThat(result).isInstanceOf(WeatherResult.Success::class.java)
        val success = result as WeatherResult.Success
        assertThat(success.weather.cityName).isEqualTo("Austin")
        verify(preferences).saveLastCity("Austin")
    }

    @Test
    fun `fetchByCity maps 404 to CityNotFound`() = runTest {
        whenever(api.getWeatherByCity(any(), any(), any()))
            .thenThrow(httpException(404))

        val result = repository.fetchByCity("Nowhere")
        assertThat(result).isEqualTo(WeatherResult.Failure(WeatherFailure.CityNotFound))
    }

    @Test
    fun `fetchByCity maps IOException to Network`() = runTest {
        // thenThrow(IOException) fails on Kotlin mocks (checked exception not declared on the
        // generated bridge); thenAnswer lets us throw it at call time instead.
        whenever(api.getWeatherByCity(any(), any(), any())).thenAnswer {
            throw IOException("offline")
        }

        val result = repository.fetchByCity("Austin")
        assertThat(result).isEqualTo(WeatherResult.Failure(WeatherFailure.Network))
    }

    @Test
    fun `fetchByCity fails fast when API key is missing`() = runTest {
        val noKeyConfig = object : OpenWeatherConfig {
            override val apiKey: String = ""
            override val iconBaseUrl: String = "https://openweathermap.org/img/wn/"
        }
        repository = WeatherRepository(api, preferences, noKeyConfig)

        val result = repository.fetchByCity("Austin")
        assertThat(result).isEqualTo(WeatherResult.Failure(WeatherFailure.MissingApiKey))
        verify(api, never()).getWeatherByCity(any(), any(), any())
    }

    @Test
    fun `fetchByCoordinates saves city from response`() = runTest {
        whenever(api.getWeatherByCoordinates(eq(30.27), eq(-97.74), eq("test-key"), any()))
            .thenReturn(sampleDto("Austin"))

        val result = repository.fetchByCoordinates(30.27, -97.74)

        assertThat(result).isInstanceOf(WeatherResult.Success::class.java)
        verify(preferences).saveLastCity("Austin")
    }

    private fun sampleDto(city: String) = WeatherResponseDto(
        cityName = city,
        weather = listOf(
            WeatherConditionDto(main = "Clear", description = "clear sky", icon = "01d")
        ),
        main = MainDto(
            temp = 70.0,
            feelsLike = 68.0,
            tempMin = 65.0,
            tempMax = 75.0,
            humidity = 40,
            pressure = 1015
        ),
        wind = WindDto(speed = 5.0, deg = 90),
        sys = SysDto(country = "US"),
        visibility = 10000,
        cod = 200,
        message = null
    )

    private fun httpException(code: Int): HttpException {
        val body = "{}".toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Any>(code, body))
    }
}
