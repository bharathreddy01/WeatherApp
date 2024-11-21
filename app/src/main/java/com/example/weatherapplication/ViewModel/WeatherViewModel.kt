package com.example.weatherapplication.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weatherState: StateFlow<WeatherState> = _weatherState

    fun fetchWeather() {
        viewModelScope.launch {
            try {
                // Simulated API call result
                val weatherData = getWeatherData() // Replace with actual API call logic
                _weatherState.value = WeatherState.Success(
                    temperature = weatherData.temperature,
                    description = weatherData.description
                )
            } catch (e: Exception) {
                _weatherState.value = WeatherState.Error("Unable to fetch weather")
            }
        }
    }

    // Mock function simulating weather data retrieval, replace with actual API call
    private fun getWeatherData(): WeatherData {
        return WeatherData(temperature = "25°C", description = "Sunny")
    }
}

// Representing weather data returned from the API
data class WeatherData(val temperature: String, val description: String)

// State sealed class
sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val temperature: String, val description: String) : WeatherState()
    data class Error(val message: String) : WeatherState()
}
