package com.example.weatherapplication.ui.weather

import com.example.weatherapplication.data.location.DeviceCoordinates
import com.example.weatherapplication.data.location.LocationProvider
import com.example.weatherapplication.data.model.WeatherInfo
import com.example.weatherapplication.data.repository.WeatherFailure
import com.example.weatherapplication.data.repository.WeatherRepository
import com.example.weatherapplication.data.repository.WeatherResult
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: WeatherRepository
    private lateinit var locationProvider: LocationProvider
    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = mock()
        locationProvider = mock()
        viewModel = WeatherViewModel(repository, locationProvider)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `search loads weather for typed city`() = runTest {
        whenever(repository.fetchByCity("Seattle")).thenReturn(WeatherResult.Success(sampleWeather("Seattle")))

        viewModel.onQueryChange("Seattle")
        viewModel.search()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.weather?.cityName).isEqualTo("Seattle")
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `search surfaces user-friendly error message`() = runTest {
        whenever(repository.fetchByCity("Nowhere"))
            .thenReturn(WeatherResult.Failure(WeatherFailure.CityNotFound))

        viewModel.onQueryChange("Nowhere")
        viewModel.search()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.weather).isNull()
        assertThat(viewModel.uiState.value.errorMessage).contains("City not found")
    }

    @Test
    fun `location permission granted loads coordinates weather`() = runTest {
        whenever(locationProvider.getCurrentCoordinates())
            .thenReturn(DeviceCoordinates(47.6, -122.3))
        whenever(repository.fetchByCoordinates(47.6, -122.3))
            .thenReturn(WeatherResult.Success(sampleWeather("Seattle")))

        viewModel.onLocationPermissionResult(granted = true)
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.weather?.cityName).isEqualTo("Seattle")
        verify(repository, never()).fetchByCity(any())
    }

    @Test
    fun `denied permission falls back to last city`() = runTest {
        whenever(repository.getLastCity()).thenReturn("Denver")
        whenever(repository.fetchByCity("Denver"))
            .thenReturn(WeatherResult.Success(sampleWeather("Denver")))

        viewModel.onLocationPermissionResult(granted = false)
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.query).isEqualTo("Denver")
        assertThat(viewModel.uiState.value.weather?.cityName).isEqualTo("Denver")
        verify(locationProvider, never()).getCurrentCoordinates()
    }

    @Test
    fun `denied permission with no last city leaves idle search state`() = runTest {
        whenever(repository.getLastCity()).thenReturn(null)

        viewModel.onLocationPermissionResult(granted = false)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isBootstrapping).isFalse()
        assertThat(state.isLoading).isFalse()
        assertThat(state.weather).isNull()
        assertThat(state.errorMessage).isNull()
    }

    private fun sampleWeather(city: String) = WeatherInfo(
        cityName = city,
        countryCode = "US",
        temperatureF = 70.0,
        feelsLikeF = 68.0,
        tempMinF = 60.0,
        tempMaxF = 75.0,
        humidityPercent = 40,
        pressureHpa = 1015,
        windSpeedMph = 5.0,
        description = "Clear sky",
        conditionMain = "Clear",
        iconCode = "01d",
        iconUrl = "https://openweathermap.org/img/wn/01d@2x.png"
    )
}
