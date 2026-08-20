package com.example.weatherapplication.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

data class DeviceCoordinates(val latitude: Double, val longitude: Double)

/**
 * Thin wrapper around Fused Location so ViewModels stay free of Play Services types.
 */
interface LocationProvider {
    fun hasLocationPermission(): Boolean
    suspend fun getCurrentCoordinates(): DeviceCoordinates?
}

@Singleton
class FusedLocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationProvider {

    private val client = LocationServices.getFusedLocationProviderClient(context)

    override fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentCoordinates(): DeviceCoordinates? {
        if (!hasLocationPermission()) return null

        return try {
            // Prefer a fresh reading; fall back to last known if current fails.
            val cancellation = CancellationTokenSource()
            val current = client.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cancellation.token
            ).await()
            current?.toCoordinates()
                ?: client.lastLocation.await()?.toCoordinates()
        } catch (_: Exception) {
            // given more time I would surface a typed LocationFailure instead of null
            null
        }
    }

    private fun Location.toCoordinates() =
        DeviceCoordinates(latitude = latitude, longitude = longitude)
}
