package com.example.weatherapplication.ui.weather

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.weatherapplication.data.model.WeatherInfo
import com.example.weatherapplication.ui.theme.InkMuted
import com.example.weatherapplication.ui.theme.InkPrimary
import com.example.weatherapplication.ui.theme.SkyDeep
import com.example.weatherapplication.ui.theme.SkyLight
import com.example.weatherapplication.ui.theme.SkyMid
import com.example.weatherapplication.ui.theme.SunAccent
import com.example.weatherapplication.ui.theme.TileSurface
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun WeatherRoute(
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val granted = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.onLocationPermissionResult(granted)
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    WeatherScreen(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onSearch = viewModel::search,
        onRetry = viewModel::retry
    )
}

@Composable
fun WeatherScreen(
    state: WeatherUiState,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onRetry: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val skyBrush = Brush.verticalGradient(
        colors = listOf(SkyLight, SkyMid, SkyDeep)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(skyBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.WbSunny,
                    contentDescription = null,
                    tint = SunAccent,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Weather",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("US city") },
                    placeholder = { Text("e.g. Seattle") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = TileSurface,
                        unfocusedContainerColor = TileSurface,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedLabelColor = InkPrimary,
                        unfocusedLabelColor = InkMuted,
                        cursorColor = SkyDeep,
                        focusedTextColor = InkPrimary,
                        unfocusedTextColor = InkPrimary
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            focusManager.clearFocus()
                            onSearch()
                        }
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                FilledIconButton(
                    onClick = {
                        focusManager.clearFocus()
                        onSearch()
                    },
                    enabled = !state.isLoading,
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = SunAccent,
                        contentColor = InkPrimary
                    ),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            when {
                state.isLoading || state.isBootstrapping -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                state.errorMessage != null && state.weather == null -> {
                    ErrorContent(message = state.errorMessage, onRetry = onRetry)
                }

                state.weather != null -> {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically { it / 4 }
                    ) {
                        WeatherContent(weather = state.weather)
                    }
                    if (state.errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = state.errorMessage,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                else -> {
                    Text(
                        text = "Search for a US city, or allow location access to load weather nearby.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherContent(weather: WeatherInfo) {
    val locationLabel = buildString {
        append(weather.cityName)
        weather.countryCode?.let { append(", ").append(it) }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = locationLabel,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.22f))
        ) {
            // Coil caches icons in memory + disk by default.
            AsyncImage(
                model = weather.iconUrl,
                contentDescription = weather.description,
                modifier = Modifier.size(104.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${weather.temperatureF.roundToInt()}°",
            style = MaterialTheme.typography.displayLarge,
            color = Color.White
        )
        Text(
            text = weather.description,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White.copy(alpha = 0.92f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "H ${weather.tempMaxF.roundToInt()}°  ·  L ${weather.tempMinF.roundToInt()}°",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White.copy(alpha = 0.85f)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.Thermostat,
                label = "Feels like",
                value = "${weather.feelsLikeF.roundToInt()}°F",
                accent = SunAccent
            )
            MetricTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.Air,
                label = "Wind",
                value = String.format(Locale.US, "%.1f mph", weather.windSpeedMph),
                accent = SkyLight
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.Speed,
                label = "Pressure",
                value = "${weather.pressureHpa} hPa",
                accent = SkyDeep
            )
            HumidityTile(
                modifier = Modifier.weight(1f),
                humidity = weather.humidityPercent
            )
        }
    }
}

@Composable
private fun MetricTile(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    accent: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(TileSurface)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = InkMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = InkPrimary
        )
    }
}

@Composable
private fun HumidityTile(
    modifier: Modifier = Modifier,
    humidity: Int
) {
    val progress = (humidity.coerceIn(0, 100)) / 100f
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(TileSurface)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(SkyMid.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.WaterDrop,
                contentDescription = null,
                tint = SkyMid,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Humidity",
            style = MaterialTheme.typography.labelMedium,
            color = InkMuted
        )
        Text(
            text = "$humidity%",
            style = MaterialTheme.typography.titleLarge,
            color = InkPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(8.dp)),
            color = SkyMid,
            trackColor = SkyMid.copy(alpha = 0.15f)
        )
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(TileSurface)
            .padding(24.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.CloudOff,
            contentDescription = null,
            tint = SkyDeep,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            color = InkPrimary,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = SkyMid,
                contentColor = Color.White
            )
        ) {
            Text("Retry")
        }
    }
}
