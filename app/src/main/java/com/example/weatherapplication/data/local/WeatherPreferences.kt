package com.example.weatherapplication.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persists the last successfully searched US city so we can auto-load it on launch
 * when location permission is denied or unavailable.
 */
interface WeatherPreferences {
    fun getLastCity(): String?
    fun saveLastCity(city: String)
}

@Singleton
class SharedWeatherPreferences @Inject constructor(
    @ApplicationContext context: Context
) : WeatherPreferences {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getLastCity(): String? =
        prefs.getString(KEY_LAST_CITY, null)?.takeIf { it.isNotBlank() }

    override fun saveLastCity(city: String) {
        prefs.edit().putString(KEY_LAST_CITY, city.trim()).apply()
    }

    private companion object {
        const val PREFS_NAME = "weather_prefs"
        const val KEY_LAST_CITY = "last_city"
    }
}
