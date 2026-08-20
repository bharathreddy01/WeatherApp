package com.example.weatherapplication.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.data.location.LocationProvider
import com.example.weatherapplication.data.model.WeatherInfo
import com.example.weatherapplication.data.repository.WeatherFailure
import com.example.weatherapplication.data.repository.WeatherRepository
import com.example.weatherapplication.data.repository.WeatherResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeatherUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val weather: WeatherInfo? = null,
    val errorMessage: String? = null,
    /** True until we finish the launch bootstrap (location / last city). */
    val isBootstrapping: Boolean = true
)

/**
 * Holds search + weather presentation state.
 * Location permission results are delivered from the Activity/Compose layer via
 * [onLocationPermissionResult] so this class stays free of Android permission APIs.
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    fun onQueryChange(value: String) {
        _uiState.update { it.copy(query = value, errorMessage = null) }
    }

    fun search() {
        val city = _uiState.value.query
        viewModelScope.launch { loadByCity(city) }
    }

    fun retry() {
        val city = _uiState.value.query.ifBlank { repository.getLastCity().orEmpty() }
        if (city.isNotBlank()) {
            viewModelScope.launch { loadByCity(city) }
        } else {
            // Re-run bootstrap path when we have nothing to retry against.
            onLocationPermissionResult(granted = locationProvider.hasLocationPermission())
        }
    }

    /**
     * Launch flow:
     * 1) If location permission granted -> weather by coordinates
     * 2) Else if a last city exists -> weather for that city
     * 3) Else idle search screen
     */
    fun onLocationPermissionResult(granted: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isBootstrapping = true, isLoading = true, errorMessage = null) }

            if (granted) {
                val coords = locationProvider.getCurrentCoordinates()
                if (coords != null) {
                    when (val result = repository.fetchByCoordinates(coords.latitude, coords.longitude)) {
                        is WeatherResult.Success -> {
                            publishSuccess(result.weather)
                            return@launch
                        }
                        is WeatherResult.Failure -> {
                            // Fall through to last-city fallback instead of hard-failing launch.
                        }
                    }
                }
            }

            val lastCity = repository.getLastCity()
            if (!lastCity.isNullOrBlank()) {
                _uiState.update { it.copy(query = lastCity) }
                loadByCity(lastCity)
            } else {
                _uiState.update {
                    it.copy(
                        isBootstrapping = false,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
        }
    }

    private suspend fun loadByCity(city: String) {
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                query = city
            )
        }

        when (val result = repository.fetchByCity(city)) {
            is WeatherResult.Success -> publishSuccess(result.weather)
            is WeatherResult.Failure -> publishFailure(result.reason)
        }
    }

    private fun publishSuccess(weather: WeatherInfo) {
        _uiState.update {
            it.copy(
                isBootstrapping = false,
                isLoading = false,
                weather = weather,
                query = weather.cityName,
                errorMessage = null
            )
        }
    }

    private fun publishFailure(failure: WeatherFailure) {
        _uiState.update {
            it.copy(
                isBootstrapping = false,
                isLoading = false,
                errorMessage = failure.toUserMessage()
            )
        }
    }
}

fun WeatherFailure.toUserMessage(): String = when (this) {
    WeatherFailure.MissingApiKey ->
        "Missing OpenWeather API key. Add OPENWEATHER_API_KEY to local.properties and rebuild."
    WeatherFailure.EmptyCity ->
        "Please enter a US city name."
    WeatherFailure.CityNotFound ->
        "City not found. Try a US city like \"Austin\" or \"Austin,TX,US\"."
    WeatherFailure.Network ->
        "Network error. Check your connection and try again."
    WeatherFailure.Unauthorized ->
        "API key was rejected. Verify OPENWEATHER_API_KEY in local.properties."
    is WeatherFailure.Unknown ->
        detail?.let { "Something went wrong ($it)." } ?: "Something went wrong. Please try again."
}
