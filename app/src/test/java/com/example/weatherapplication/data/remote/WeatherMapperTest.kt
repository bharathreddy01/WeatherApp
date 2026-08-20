package com.example.weatherapplication.data.remote

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class WeatherMapperTest {

    private val iconBase = "https://openweathermap.org/img/wn/"

    @Test
    fun `maps a complete OpenWeather payload`() {
        val dto = WeatherResponseDto(
            cityName = "Austin",
            weather = listOf(
                WeatherConditionDto(
                    main = "Clouds",
                    description = "scattered clouds",
                    icon = "03d"
                )
            ),
            main = MainDto(
                temp = 78.5,
                feelsLike = 80.0,
                tempMin = 72.0,
                tempMax = 84.0,
                humidity = 55,
                pressure = 1012
            ),
            wind = WindDto(speed = 7.2, deg = 180),
            sys = SysDto(country = "US"),
            visibility = 10000,
            cod = 200,
            message = null
        )

        val info = WeatherMapper.toWeatherInfo(dto, iconBase)

        assertThat(info).isNotNull()
        assertThat(info!!.cityName).isEqualTo("Austin")
        assertThat(info.countryCode).isEqualTo("US")
        assertThat(info.temperatureF).isEqualTo(78.5)
        assertThat(info.description).isEqualTo("Scattered clouds")
        assertThat(info.iconUrl).isEqualTo("https://openweathermap.org/img/wn/03d@2x.png")
    }

    @Test
    fun `returns null when temperature is missing`() {
        val dto = WeatherResponseDto(
            cityName = "Austin",
            weather = listOf(
                WeatherConditionDto(main = "Clear", description = "clear sky", icon = "01d")
            ),
            main = MainDto(
                temp = null,
                feelsLike = null,
                tempMin = null,
                tempMax = null,
                humidity = null,
                pressure = null
            ),
            wind = null,
            sys = SysDto(country = "US"),
            visibility = null,
            cod = 200,
            message = null
        )

        assertThat(WeatherMapper.toWeatherInfo(dto, iconBase)).isNull()
    }

    @Test
    fun `buildIconUrl normalizes missing trailing slash`() {
        val url = WeatherMapper.buildIconUrl("https://openweathermap.org/img/wn", "01d")
        assertThat(url).isEqualTo("https://openweathermap.org/img/wn/01d@2x.png")
    }
}
