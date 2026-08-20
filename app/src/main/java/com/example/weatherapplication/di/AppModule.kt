package com.example.weatherapplication.di

import com.example.weatherapplication.BuildConfig
import com.example.weatherapplication.data.local.SharedWeatherPreferences
import com.example.weatherapplication.data.local.WeatherPreferences
import com.example.weatherapplication.data.location.FusedLocationProvider
import com.example.weatherapplication.data.location.LocationProvider
import com.example.weatherapplication.data.remote.BuildConfigOpenWeatherConfig
import com.example.weatherapplication.data.remote.OpenWeatherConfig
import com.example.weatherapplication.data.remote.WeatherApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingsModule {

    @Binds
    @Singleton
    abstract fun bindWeatherPreferences(impl: SharedWeatherPreferences): WeatherPreferences

    @Binds
    @Singleton
    abstract fun bindLocationProvider(impl: FusedLocationProvider): LocationProvider

    @Binds
    @Singleton
    abstract fun bindOpenWeatherConfig(impl: BuildConfigOpenWeatherConfig): OpenWeatherConfig
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            // given more time I'd gate BODY logging behind BuildConfig.DEBUG only via a flavor
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.OPENWEATHER_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }
}
