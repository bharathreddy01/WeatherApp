package com.example.weatherapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapplication.ui.theme.screens.ErrorScreen
import com.example.weatherapplication.ui.theme.screens.WeatherScreen
import com.example.weatherapplication.ViewModel.WeatherViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import com.example.weatherapplication.ViewModel.WeatherState


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: WeatherViewModel = viewModel()
            val weatherState = viewModel.weatherState.collectAsState()

            // Exhaustive when expression
            when (val state = weatherState.value) {
                is WeatherState.Loading -> {
                    CircularProgressIndicator()
                }
                is WeatherState.Success -> {
                    WeatherScreen(
                        temperature = state.temperature,
                        description = state.description
                    ) {
                        viewModel.fetchWeather() // Retry action
                    }
                }
                is WeatherState.Error -> {
                    ErrorScreen(
                        errorMessage = state.message
                    ) {
                        viewModel.fetchWeather() // Retry action
                    }
                }
                else -> {
                    // Handle unexpected states
                    CircularProgressIndicator() // or an empty composable
                }
            }
        }
    }
}
