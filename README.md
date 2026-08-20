# Weather Application

Android weather app (Kotlin) that looks up current conditions for US cities via
[OpenWeatherMap](https://openweathermap.org/).

## Features

- Search by US city name
- Current temperature, feels-like, high/low, humidity, pressure, wind, and condition text
- Weather icon from OpenWeather (cached by Coil)
- Requests location permission on launch; if granted, loads weather for the device location
- Remembers the last successful city and auto-loads it when location is unavailable
- MVVM + Hilt + Retrofit + Coroutines + Jetpack Compose
- JUnit unit tests for mapper, repository, and ViewModel

## Setup

1. Create a free API key at [openweathermap.org](https://openweathermap.org/api).
2. Add the key to `local.properties` in the project root (this file is gitignored):

```properties
OPENWEATHER_API_KEY=your_api_key_here
sdk.dir=/path/to/Android/sdk
```

3. Use **JDK 17** (required for the current Kotlin/Hilt/kapt toolchain).
4. Open the project in Android Studio and run the `app` configuration.

> New OpenWeather keys can take a short time to activate after signup.

## Architecture

| Layer | Responsibility |
| --- | --- |
| UI (`ui/weather`) | Compose screens + `WeatherViewModel` state |
| Domain/data models | `WeatherInfo` independent of JSON |
| Repository | API calls, error mapping, last-city persistence |
| Remote | Retrofit service + DTOs + mapper |
| Location | Fused Location wrapper behind `LocationProvider` |
| DI | Hilt modules bind interfaces and provide Retrofit |

## Run unit tests

```bash
./gradlew :app:testDebugUnitTest
```

## Notes

- City search uses `q={city},US` (challenge-allowed city-name endpoint). Location uses `lat` / `lon`.
- Units are imperial (°F, mph) for a US-focused search experience.
- If `OPENWEATHER_API_KEY` is missing, the UI shows a clear setup message instead of failing silently.
